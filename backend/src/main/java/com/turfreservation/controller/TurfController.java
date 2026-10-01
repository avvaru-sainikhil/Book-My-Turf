package com.turfreservation.controller;

import com.turfreservation.dto.TurfRequest;
import com.turfreservation.dto.TurfResponse;
import com.turfreservation.entity.User;
import com.turfreservation.service.TurfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turfs")
public class TurfController {

    @Autowired
    private TurfService turfService;

    @PostMapping("/owner/add")
    public ResponseEntity<TurfResponse> addTurf(@AuthenticationPrincipal User owner,
                                                 @Valid @RequestBody TurfRequest request) {
        TurfResponse response = turfService.addTurf(owner.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TurfResponse>> getAllTurfs() {
        return ResponseEntity.ok(turfService.getAllTurfs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurfResponse> getTurfById(@PathVariable Long id) {
        return ResponseEntity.ok(turfService.getTurfById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TurfResponse>> searchTurfs(@RequestParam String keyword) {
        return ResponseEntity.ok(turfService.searchTurfs(keyword));
    }

    @GetMapping("/search/location")
    public ResponseEntity<List<TurfResponse>> searchByLocation(@RequestParam String location) {
        return ResponseEntity.ok(turfService.searchByLocation(location));
    }

    @GetMapping("/owner/my-turfs")
    public ResponseEntity<List<TurfResponse>> getOwnerTurfs(@AuthenticationPrincipal User owner) {
        return ResponseEntity.ok(turfService.getOwnerTurfs(owner.getId()));
    }

    @PutMapping("/owner/{turfId}")
    public ResponseEntity<TurfResponse> updateTurf(@PathVariable Long turfId,
                                                     @AuthenticationPrincipal User owner,
                                                     @Valid @RequestBody TurfRequest request) {
        TurfResponse response = turfService.updateTurf(turfId, owner.getId(), request);
        return ResponseEntity.ok(response);
    }
}