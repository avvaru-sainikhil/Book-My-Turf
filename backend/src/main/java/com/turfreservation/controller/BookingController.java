package com.turfreservation.controller;

import com.turfreservation.dto.BookingRequest;
import com.turfreservation.dto.BookingResponse;
import com.turfreservation.entity.User;
import com.turfreservation.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingResponse> createBooking(@AuthenticationPrincipal User customer,
                                                            @Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(customer.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long bookingId,
                                                            @AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, customer.getId()));
    }
    @GetMapping("/customer/history")
    public ResponseEntity<List<BookingResponse>> getCustomerHistory(@AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(bookingService.getCustomerBookings(customer.getId()));
    }

    @GetMapping("/owner/requests")
    public ResponseEntity<List<BookingResponse>> getOwnerRequests(@AuthenticationPrincipal User owner) {
        return ResponseEntity.ok(bookingService.getOwnerBookingRequests(owner.getId()));
    }

    @PutMapping("/{bookingId}/accept")
    public ResponseEntity<BookingResponse> acceptBooking(@PathVariable Long bookingId,
                                                            @AuthenticationPrincipal User owner) {
        return ResponseEntity.ok(bookingService.acceptBooking(bookingId, owner.getId()));
    }

    @PutMapping("/{bookingId}/reject")
    public ResponseEntity<BookingResponse> rejectBooking(@PathVariable Long bookingId,
                                                            @AuthenticationPrincipal User owner) {
        return ResponseEntity.ok(bookingService.rejectBooking(bookingId, owner.getId()));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingDetails(@PathVariable Long bookingId,
                                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingService.getBookingDetails(bookingId, user.getId()));
    }
}