package com.nursematch.provider.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "provider_profiles")
@CompoundIndex(def = "{'specialties': 1, 'state': 1, 'acceptingStudents': 1}")
@Data
public class ProviderProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;        // FK → User._id

    private String credentials;   // "NP", "MD", "DO"
    private String licenseState;

    private List<String> specialties = new ArrayList<>();

    private String city;
    private String state;
    private double lat;
    private double lng;

    private boolean acceptingStudents = true;

    private List<AvailabilitySlot> availability = new ArrayList<>();
}