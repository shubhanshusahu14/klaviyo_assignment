package org.example.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {

    private String email;
    private String orderId;
    private double amount;
    private String currency;

}

