package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetricData {
    private String type;
    private MetricAttributes attributes;
}