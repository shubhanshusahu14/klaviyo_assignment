package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventAttributes {
    private Metric metric;
    private Profile profile;
    private EventProperties properties;
}