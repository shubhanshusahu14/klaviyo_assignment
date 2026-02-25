package org.example;

import org.example.config.ConfigProvider;
import org.example.exception.KlaviyoClientException;
import org.example.model.EventRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class KlaviyoNullArgumentTest {

    @Test
    void testSendOrderEvent_MissingEmail_ShouldThrowException() {

        // Provide real config using lambda
        ConfigProvider provider = key -> {
            switch (key) {
                case "klaviyo.base.url":
                    return "https://a.klaviyo.com/api";
                case "klaviyo.private.key":
                    return "pk_c8cecae0850be1fa7585711f4ff7fb560d";
                case "klaviyo.revision":
                    return "2024-02-15";
                default:
                    return null;
            }
        };

        KlaviyoClient client = new KlaviyoClient(provider);

        // Missing email (null)
        EventRequest request = new EventRequest(
                null,                 // email missing
                "ORD999",
                100.0,
                "INR"
        );

        // Expect KlaviyoClientException
        assertThrows(KlaviyoClientException.class, () -> {
            client.sendOrderEvent(request);
        });
    }
}