package org.example;

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

        EventRequest request = new EventRequest(
                "testuser@email.com",
                "ORD123",
                499.0,
                "INR"
        );

//        client.sendOrderEvent(request);

//        client.getOrderEventsByEmail("testuser@email.com");

        client.getAllEvents();

    }

}
