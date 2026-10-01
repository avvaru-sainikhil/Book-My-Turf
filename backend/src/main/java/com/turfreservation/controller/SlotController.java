package com.turfreservation.controller;

import com.turfreservation.dto.SlotResponse;
import com.turfreservation.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    @Autowired
    private SlotService slotService;

    @GetMapping("/{turfId}")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @PathVariable Long turfId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(slotService.getAvailableSlots(turfId, date));
    }
}