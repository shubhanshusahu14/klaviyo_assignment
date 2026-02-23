package org.example;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.flogger.FluentLogger;
import com.google.gson.Gson;
import org.example.exception.KlaviyoApiException;
import org.example.exception.KlaviyoAuthException;
import org.example.exception.KlaviyoClientException;
import org.example.exception.KlaviyoServerException;
import org.example.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class KlaviyoClient {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();
    private final HttpTransport transport = new NetHttpTransport();
    private final Gson gson = new Gson();

    private final ConfigProvider configProvider;

    public KlaviyoClient(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }


    private void handleKlaviyoError(HttpResponseException e) {

        int code = e.getStatusCode();
        String message = e.getStatusMessage();

        logger.atSevere().log(
                "Klaviyo API Error [%d]: %s",
                code,
                e.getContent()
        );

        switch (code) {

            case 401, 403 -> throw new KlaviyoAuthException("Authentication failed.", code);

            case 404 -> throw new KlaviyoClientException("Resource not found.", code);

            case 409 -> throw new KlaviyoClientException("Duplicate resource.", code);

            case 429 -> throw new KlaviyoServerException("Rate limit exceeded.", code);

            default -> {
                if (code >= 500) {
                    throw new KlaviyoServerException("Klaviyo server error.", code);
                } else if (code >= 400) {
                    throw new KlaviyoClientException("Client error: " + message, code);
                } else {
                    throw new KlaviyoApiException("Unexpected error.", code);
                }
            }
        }
    }



    private void applyAuthentication(HttpRequest request) {
        request.getHeaders().set(
                "Authorization",
                "Klaviyo-API-Key " + configProvider.getProperty("klaviyo.private.key")
        );
        request.getHeaders().set(
                "Revision",
                configProvider.getProperty("klaviyo.revision")
        );
    }



    // GET METHOD

    public void getOrderEventsByEmail(String email) throws IOException {

        String baseUrl = configProvider.getProperty("klaviyo.base.url");
        String apiKey = configProvider.getProperty("klaviyo.private.key");
        String revision = configProvider.getProperty("klaviyo.revision");

        // STEP 1: GET PROFILE BY EMAIL

        GenericUrl profileUrl = new GenericUrl(baseUrl + "/profiles/");
        profileUrl.put("filter", "equals(email,\"" + email + "\")");

        HttpRequestFactory factory = transport.createRequestFactory();
        HttpRequest profileRequest = factory.buildGetRequest(profileUrl);

        applyAuthentication(profileRequest);

        logger.atInfo().log("Fetching profile for email: %s", email);

        HttpResponse profileResponse = profileRequest.execute();
        String profileBody = profileResponse.parseAsString();

        logger.atInfo().log("Profile Response Code: %s", profileResponse.getStatusCode());
        logger.atInfo().log("Profile Response Body: %s", profileBody);

        // STEP 2: EXTRACT PROFILE ID

        Map<?, ?> profileMap = gson.fromJson(profileBody, Map.class);

        String profileId = null;

        try {
            Map<?, ?> data = (Map<?, ?>) ((java.util.List<?>) profileMap.get("data")).get(0);
            profileId = (String) data.get("id");
        } catch (Exception e) {
            logger.atSevere().log("Profile not found for email: %s", email);
            return;
        }

        logger.atInfo().log("Extracted Profile ID: %s", profileId);

        //  STEP 3: GET EVENTS USING PROFILE ID

        GenericUrl eventsUrl = new GenericUrl(baseUrl + "/events/");
        eventsUrl.put("filter", "equals(profile_id,\"" + profileId + "\")");

        HttpRequest eventsRequest = factory.buildGetRequest(eventsUrl);

        eventsRequest.getHeaders().set("Authorization", "Klaviyo-API-Key " + apiKey);
        eventsRequest.getHeaders().set("Revision", revision);

        logger.atInfo().log("Fetching events for profile ID: %s", profileId);

        HttpResponse eventsResponse = eventsRequest.execute();
        String eventsBody = eventsResponse.parseAsString();

        logger.atInfo().log("Events Response Code: %s", eventsResponse.getStatusCode());
        logger.atInfo().log("Events Response Body: %s", eventsBody);
    }


    // GET ALL EVENT WITHOUT PARTICULAR EMAIL

    public void getAllEvents() throws IOException {

        String baseUrl = configProvider.getProperty("klaviyo.base.url");
        GenericUrl url = new GenericUrl(baseUrl + "/events/");

        HttpRequestFactory factory = transport.createRequestFactory();
        HttpRequest request = factory.buildGetRequest(url);

        applyAuthentication(request);

        logger.atInfo().log("Fetching all events");

        try {

            HttpResponse response = request.execute();
            String body = response.parseAsString();

            logger.atInfo().log("Response Code: %s", response.getStatusCode());
            logger.atInfo().log("Response Body: %s", body);

        } catch (HttpResponseException e) {

            handleKlaviyoError(e);

        }

    }


    //  POST METHOD

    public void sendOrderEvent(EventRequest eventReq) throws IOException {


        String baseUrl = configProvider.getProperty("klaviyo.base.url");
        String apiKey = configProvider.getProperty("klaviyo.private.key");
        String revision = configProvider.getProperty("klaviyo.revision");



        String url = baseUrl + "/events/";

        String jsonBody = buildEventJson(eventReq);

        HttpContent content = new ByteArrayContent(
                "application/json",
                jsonBody.getBytes(StandardCharsets.UTF_8)
        );

        HttpRequestFactory factory = transport.createRequestFactory();
        HttpRequest request = factory.buildPostRequest(new GenericUrl(url), content);

        applyAuthentication(request);

        logger.atInfo().log("Sending POST request to: %s", url);
        logger.atInfo().log("Request JSON: %s", jsonBody);

        try {

            HttpResponse response = request.execute();
            String body = response.parseAsString();

            logger.atInfo().log("Response Code: %s", response.getStatusCode());
            logger.atInfo().log("Response Body: %s", body);

        } catch (HttpResponseException e) {

            handleKlaviyoError(e);

        }
    }

    // -------------------- JSON BUILDER --------------------

    private String buildEventJson(EventRequest req) {

        MetricAttributes metricAttr = new MetricAttributes("Placed Order");
        MetricData metricData = new MetricData("metric", metricAttr);
        Metric metric = new Metric(metricData);

        ProfileAttributes profileAttr = new ProfileAttributes(req.getEmail());
        ProfileData profileData = new ProfileData("profile", profileAttr);
        Profile profile = new Profile(profileData);

        EventProperties properties = new EventProperties(
                req.getOrderId(),
                req.getAmount(),
                req.getCurrency()
        );

        EventAttributes attributes = new EventAttributes(metric, profile, properties);

        EventData data = new EventData("event", attributes);

        EventPayload payload = new EventPayload(data);

        return gson.toJson(payload);
    }
}



//String a = """
//        {
//            "data": {
//                "type": "event",
//                "attributes": {
//                        "metric": {
//                            "data": {
//                                "type": "metric",
//                                "attributes": {
//                                    "name": "Placed Order"
//                                }
//                            }
//                        },
//                        "profile": {
//                            "data": {
//                                "type": "profile",
//                                "attributes": {
//                                    "email": "abc@email.com"
//                                }
//                            }
//                        },
//                        "properties": {
//                            "order_id": "ORD123",
//                            "amount": 499,
//                            "currency": "INR"
//                        }
//                }
//            }
//        }
//        """;