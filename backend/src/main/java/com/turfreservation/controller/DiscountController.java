package com.turfreservation.controller;

import com.turfreservation.dto.DiscountRequest;
import com.turfreservation.dto.DiscountResponse;
import com.turfreservation.entity.User;
import com.turfreservation.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @PostMapping("/owner/{turfId}/add")
    public ResponseEntity<DiscountResponse> addDiscount(@PathVariable Long turfId,
                                                           @AuthenticationPrincipal User owner,
                                                           @Valid @RequestBody DiscountRequest request) {
        DiscountResponse response = discountService.addDiscount(turfId, owner.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/turf/{turfId}")
    public ResponseEntity<List<DiscountResponse>> getTurfDiscounts(@PathVariable Long turfId) {
        return ResponseEntity.ok(discountService.getTurfDiscounts(turfId));
    }

    @DeleteMapping("/owner/{discountId}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long discountId,
                                                 @AuthenticationPrincipal User owner) {
        discountService.deleteDiscount(discountId, owner.getId());
        return ResponseEntity.noContent().build();
    }
}