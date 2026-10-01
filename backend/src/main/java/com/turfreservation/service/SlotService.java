package com.turfreservation.service;

import com.turfreservation.dto.SlotResponse;
import com.turfreservation.entity.Booking;
import com.turfreservation.entity.Turf;
import com.turfreservation.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SlotService {

    @Autowired
    private TurfService turfService;

    @Autowired
    private BookingRepository bookingRepository;

    public List<SlotResponse> getAvailableSlots(Long turfId, LocalDate date) {
        Turf turf = turfService.getTurfEntityById(turfId);

        List<Booking> acceptedBookings = bookingRepository
                .findByTurfIdAndBookingDateAndStatus(turfId, date, Booking.BookingStatus.ACCEPTED);

        Set<LocalTime> bookedStartTimes = acceptedBookings.stream()
                .map(Booking::getStartTime)
                .collect(Collectors.toSet());

        List<SlotResponse> slots = new ArrayList<>();
        LocalTime current = turf.getOpeningTime();
        int durationMinutes = turf.getSlotDuration();

        while (current.plusMinutes(durationMinutes).compareTo(turf.getClosingTime()) <= 0) {
            LocalTime slotEnd = current.plusMinutes(durationMinutes);
            boolean available = !bookedStartTimes.contains(current);

            // If date is today, also mark past time slots as unavailable
            if (date.isEqual(LocalDate.now()) && current.isBefore(LocalTime.now())) {
                available = false;
            }

            slots.add(new SlotResponse(current, slotEnd, available));
            current = slotEnd;
        }

        return slots;
    }
}
