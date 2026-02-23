package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileData {
    private String type;
    private ProfileAttributes attributes;
}