package com.turfreservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "turf_discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurfDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turf_id", nullable = false)
    private Turf turf;

    @NotBlank(message = "Discount name is required")
    @Column(nullable = false)
    private String discountName;

    @NotNull(message = "Discount date is required")
    @Column(nullable = false)
    private LocalDate discountDate;

    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(nullable = false)
    private LocalTime endTime;

    @NotNull(message = "Discount amount is required")
    @PositiveOrZero(message = "Discount amount must be zero or positive")
    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}