package com.turfreservation.repository;

import com.turfreservation.entity.Booking;
import com.turfreservation.entity.Turf;
import com.turfreservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerOrderByCreatedAtDesc(User customer);

    List<Booking> findByTurfOwnerOrderByCreatedAtDesc(User owner);

    List<Booking> findByTurfOrderByCreatedAtDesc(Turf turf);

    List<Booking> findByTurfIdAndBookingDateAndStatus(
            Long turfId, LocalDate bookingDate, Booking.BookingStatus status);

    Optional<Booking> findByBookingReferenceId(String bookingReferenceId);

    boolean existsByTurfIdAndBookingDateAndStartTimeAndStatus(
            Long turfId, LocalDate bookingDate, LocalTime startTime,
            Booking.BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.turf.owner.id = :ownerId ORDER BY b.createdAt DESC")
    List<Booking> findAllByTurfOwnerId(@Param("ownerId") Long ownerId);
}