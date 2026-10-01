package com.turfreservation.service;

import com.turfreservation.dto.DiscountRequest;
import com.turfreservation.dto.DiscountResponse;
import com.turfreservation.entity.Turf;
import com.turfreservation.entity.TurfDiscount;
import com.turfreservation.exception.BadRequestException;
import com.turfreservation.exception.ResourceNotFoundException;
import com.turfreservation.exception.UnauthorizedException;
import com.turfreservation.repository.TurfDiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountService {

    @Autowired
    private TurfDiscountRepository discountRepository;

    @Autowired
    private TurfService turfService;

    public DiscountResponse addDiscount(Long turfId, Long ownerId, DiscountRequest request) {
        Turf turf = turfService.getTurfEntityById(turfId);

        if (!turf.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("You do not own this turf");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }

        TurfDiscount discount = new TurfDiscount();
        discount.setTurf(turf);
        discount.setDiscountName(request.getDiscountName());
        discount.setDiscountDate(request.getDiscountDate());
        discount.setStartTime(request.getStartTime());
        discount.setEndTime(request.getEndTime());
        discount.setDiscountAmount(request.getDiscountAmount());

        TurfDiscount saved = discountRepository.save(discount);
        return toResponse(saved);
    }

    public List<DiscountResponse> getTurfDiscounts(Long turfId) {
        return discountRepository.findByTurfIdOrderByDiscountDateAscStartTimeAsc(turfId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteDiscount(Long discountId, Long ownerId) {
        TurfDiscount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found"));

        if (!discount.getTurf().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("You do not own this turf");
        }

        discountRepository.delete(discount);
    }

    public List<TurfDiscount> getActiveDiscountsForSlot(Long turfId, LocalDate date,
                                                          java.time.LocalTime startTime,
                                                          java.time.LocalTime endTime) {
        List<TurfDiscount> dayDiscounts = discountRepository.findByTurfIdAndDiscountDate(turfId, date);
        return dayDiscounts.stream()
                .filter(d -> !startTime.isBefore(d.getStartTime()) && !endTime.isAfter(d.getEndTime()))
                .collect(Collectors.toList());
    }

    private DiscountResponse toResponse(TurfDiscount discount) {
        return new DiscountResponse(
                discount.getId(),
                discount.getTurf().getId(),
                discount.getDiscountName(),
                discount.getDiscountDate(),
                discount.getStartTime(),
                discount.getEndTime(),
                discount.getDiscountAmount()
        );
    }
}