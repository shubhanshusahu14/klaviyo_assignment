package org.example;

import org.example.config.ConfigProvider;
import org.example.exception.KlaviyoAuthException;
import org.example.exception.KlaviyoClientException;
import org.example.model.EventRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class KlaviyoWrongAPITest {

    @Test
    void testSendOrderEvent_InvalidApiKey_ShouldThrowAuthException() {

        ConfigProvider provider = key -> {
            switch (key) {
                case "klaviyo.base.url":
                    return "https://a.klaviyo.com/api";
                case "klaviyo.private.key":
                    return "WRONG_KEY";  // intentionally wrong
                case "klaviyo.revision":
                    return "2024-02-15";
                default:
                    return null;
            }
        };

        KlaviyoClient client = new KlaviyoClient(provider);

        EventRequest request = new EventRequest(
                "test@email.com",
                "ORD123",
                200.0,
                "INR"
        );

        assertThrows(KlaviyoAuthException.class, () -> {
            client.sendOrderEvent(request);
        });
    }
}
