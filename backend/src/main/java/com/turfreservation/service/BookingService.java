package com.turfreservation.service;
import com.turfreservation.entity.TurfDiscount;
import com.turfreservation.dto.BookingRequest;
import com.turfreservation.dto.BookingResponse;
import com.turfreservation.entity.Booking;
import com.turfreservation.entity.Turf;
import com.turfreservation.entity.User;
import com.turfreservation.exception.BadRequestException;
import com.turfreservation.exception.ConflictException;
import com.turfreservation.exception.ResourceNotFoundException;
import com.turfreservation.exception.UnauthorizedException;
import com.turfreservation.repository.BookingRepository;
import com.turfreservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private DiscountService discountService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TurfService turfService;

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final Random random = new Random();

    public BookingResponse createBooking(Long customerId, BookingRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getRole() != User.Role.CUSTOMER) {
            throw new UnauthorizedException("Only customers can create bookings");
        }

        Turf turf = turfService.getTurfEntityById(request.getTurfId());

        if (request.getBookingDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot book a past date");
        }

        if (request.getBookingDate().isEqual(LocalDate.now()) && request.getStartTime().isBefore(LocalTime.now())) {
            throw new BadRequestException("Cannot book a past time slot");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }

        if (request.getStartTime().isBefore(turf.getOpeningTime()) || request.getEndTime().isAfter(turf.getClosingTime())) {
            throw new BadRequestException("Selected slot is outside turf operating hours");
        }

        boolean alreadyBooked = bookingRepository.existsByTurfIdAndBookingDateAndStartTimeAndStatus(
                request.getTurfId(), request.getBookingDate(), request.getStartTime(), Booking.BookingStatus.ACCEPTED);

        if (alreadyBooked) {
            throw new ConflictException("This slot is already booked. Please choose another slot.");
        }

        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        double price = (minutes / (double) turf.getSlotDuration()) * turf.getPricePerSlot();

        List<TurfDiscount> applicableDiscounts = discountService.getActiveDiscountsForSlot(
                request.getTurfId(), request.getBookingDate(), request.getStartTime(), request.getEndTime());

        double totalDiscount = applicableDiscounts.stream()
                .mapToDouble(TurfDiscount::getDiscountAmount)
                .sum();

        price = Math.max(0, price - totalDiscount);

        Booking booking = new Booking();
        booking.setBookingReferenceId(generateUniqueReferenceId());
        booking.setCustomer(customer);
        booking.setTurf(turf);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setPrice(price);
        booking.setStatus(Booking.BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);
        return toResponse(saved);
    }

    public List<BookingResponse> getCustomerBookings(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return bookingRepository.findByCustomerOrderByCreatedAtDesc(customer).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getOwnerBookingRequests(Long ownerId) {
        return bookingRepository.findAllByTurfOwnerId(ownerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse acceptBooking(Long bookingId, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getTurf().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("You do not own this turf");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be accepted");
        }

        boolean alreadyAccepted = bookingRepository.existsByTurfIdAndBookingDateAndStartTimeAndStatus(
                booking.getTurf().getId(), booking.getBookingDate(), booking.getStartTime(), Booking.BookingStatus.ACCEPTED);

        if (alreadyAccepted) {
            throw new ConflictException("This slot has already been accepted for another booking");
        }

        booking.setStatus(Booking.BookingStatus.ACCEPTED);
        Booking updated = bookingRepository.save(booking);
        return toResponse(updated);
    }

    public BookingResponse rejectBooking(Long bookingId, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getTurf().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("You do not own this turf");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be rejected");
        }

        booking.setStatus(Booking.BookingStatus.REJECTED);
        Booking updated = bookingRepository.save(booking);
        return toResponse(updated);
    }
    
    public BookingResponse cancelBooking(Long bookingId, Long customerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You do not own this booking");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);
        return toResponse(updated);
    }
    
    public BookingResponse getBookingDetails(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        boolean isCustomer = booking.getCustomer().getId().equals(userId);
        boolean isOwner = booking.getTurf().getOwner().getId().equals(userId);

        if (!isCustomer && !isOwner) {
            throw new UnauthorizedException("You are not authorized to view this booking");
        }

        return toResponse(booking);
    }

    private String generateUniqueReferenceId() {
        String refId;
        do {
            StringBuilder sb = new StringBuilder("TRF-");
            sb.append(LocalDate.now().getYear()).append("-");
            for (int i = 0; i < 6; i++) {
                sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
            refId = sb.toString();
        } while (bookingRepository.findByBookingReferenceId(refId).isPresent());
        return refId;
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getBookingReferenceId(),
                booking.getCustomer().getId(),
                booking.getCustomer().getName(),
                booking.getTurf().getId(),
                booking.getTurf().getTurfName(),
                booking.getTurf().getLocation(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getPrice(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}