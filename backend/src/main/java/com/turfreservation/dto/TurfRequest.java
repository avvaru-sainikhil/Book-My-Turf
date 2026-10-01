package com.turfreservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalTime;

@Data
public class TurfRequest {

    @NotBlank(message = "Turf name is required")
    private String turfName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Sport type is required")
    private String sportType;

    private String description;

    @NotNull(message = "Price per slot is required")
    @Positive(message = "Price must be positive")
    private Double pricePerSlot;

    @NotNull(message = "Slot duration is required")
    @Positive(message = "Slot duration must be positive")
    private Integer slotDuration;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;
}