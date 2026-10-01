package com.turfreservation.dto;

import com.turfreservation.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String bookingReferenceId;
    private Long customerId;
    private String customerName;
    private Long turfId;
    private String turfName;
    private String turfLocation;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double price;
    private Booking.BookingStatus status;
    private LocalDateTime createdAt;
}