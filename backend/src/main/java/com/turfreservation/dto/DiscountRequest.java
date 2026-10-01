package com.turfreservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DiscountRequest {

    @NotBlank(message = "Discount name is required")
    private String discountName;

    @NotNull(message = "Discount date is required")
    private LocalDate discountDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Discount amount is required")
    @PositiveOrZero(message = "Discount amount must be zero or positive")
    private Double discountAmount;
}