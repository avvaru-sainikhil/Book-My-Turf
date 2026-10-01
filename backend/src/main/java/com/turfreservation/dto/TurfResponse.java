package com.turfreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurfResponse {
    private Long id;
    private String turfName;
    private String location;
    private String address;
    private String sportType;
    private String description;
    private Double pricePerSlot;
    private Integer slotDuration;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Long ownerId;
    private String ownerName;
}