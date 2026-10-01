package com.turfreservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "turfs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Turf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Turf name is required")
    @Column(nullable = false)
    private String turfName;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "Sport type is required")
    @Column(nullable = false)
    private String sportType;

    @Column(length = 1000)
    private String description;

    @NotNull(message = "Price per slot is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double pricePerSlot;

    @NotNull(message = "Slot duration is required")
    @Positive(message = "Slot duration must be positive")
    @Column(nullable = false)
    private Integer slotDuration; // in minutes

    @NotNull(message = "Opening time is required")
    @Column(nullable = false)
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    @Column(nullable = false)
    private LocalTime closingTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}