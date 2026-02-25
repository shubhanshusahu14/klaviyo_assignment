package org.example;

import org.example.KlaviyoClient;
import org.example.config.ConfigProvider;
import org.example.model.EventRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PojoStructureTest {
    @Test
    void testBuildEventJson_ShouldContainAllFields() {

        // Dummy config provider (not used here but required for constructor)
        ConfigProvider provider = key -> "dummy";

        KlaviyoClient client = new KlaviyoClient(provider);

        EventRequest request = new EventRequest(
                "unit@test.com",
                "ORD777",
                555.0,
                "INR"
        );

        String json = client.buildEventJson(request);

        // Basic structure checks
        assertTrue(json.contains("\"type\":\"event\""));
        assertTrue(json.contains("\"Placed Order\""));
        assertTrue(json.contains("\"unit@test.com\""));
        assertTrue(json.contains("\"ORD777\""));
        assertTrue(json.contains("555.0"));
        assertTrue(json.contains("\"INR\""));
    }
}
