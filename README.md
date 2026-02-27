Klaviyo assignment software design document

## 1\. Overall Logic

This project implements a standalone Klaviyo API client using:

* Google HTTP Client (NetHttpTransport)  
* Gson for JSON serialization  
* Functional interface for configuration  
* Custom exception handling for API errors

# 2\. Configuration Logic

Instead of directly accessing configuration values inside the client, we created a functional interface **ClientProvider.** The KlaviyoClient receives this via constructor injection.

# 3\. Authentication Flow

Before making any API call, authentication headers are applied using **applyAuthentication(HttpRequest request)** This method sets **Authorization header (Private API key)** and **Revision header.**

# 4\. POST Event Flow

### **sendOrderEvent(EventRequest)**

EventRequest → buildEventJson()  →  Gson converts POJO to JSON  →  Create POST request   → applyAuthentication()   → Execute request   → Success → Log

 or Failure → handleKlaviyoError()

# 5\. GET Event Flow (By Email)

### **getOrderEventsByEmail(email)**

Email  → GET /profiles (filter by email)   → Extract profile\_id   → GET /events (filter by profile\_id)   → Display events

# 6\. JSON Modeling Logic

Instead of using nested `Map<String, Object>`, we created structured **POJO classes.**

# 7\. Error Handling Logic

When an API call fails, Google HTTP Client throws **`HttpResponseException`.**

Based on status code:

* 401 / 403 → KlaviyoAuthException

* 404 → KlaviyoClientException

* 429 → KlaviyoServerException

* 500+ → KlaviyoServerException

* Other 4xx → KlaviyoClientException

