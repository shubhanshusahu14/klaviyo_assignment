package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventProperties {
    private String order_id;
    private double amount;
    private String currency;
}