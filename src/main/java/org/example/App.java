package org.example;

import org.example.config.ConfigLoader;
import org.example.config.ConfigProvider;
import org.example.model.EventRequest;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws Exception {

        ConfigProvider provider = key ->
                ConfigLoader.getProperty(key);

        KlaviyoClient client = new KlaviyoClient(provider);

        EventRequest request = EventRequest.builder()
                .email("testuser@email.com")
                .orderId("ORD123")
                .amount(499.0)
                .currency("INR")
                .build();

//        client.sendOrderEvent(request);

//        client.getOrderEventsByEmail("testuser@email.com");

        client.getAllEvents();

    }

}
