package com.turfreservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResponse {
    private Long id;
    private Long turfId;
    private String discountName;
    private LocalDate discountDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double discountAmount;
}