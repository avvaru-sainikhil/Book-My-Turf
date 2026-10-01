package com.turfreservation.service;

import com.turfreservation.dto.TurfRequest;
import com.turfreservation.dto.TurfResponse;
import com.turfreservation.entity.Turf;
import com.turfreservation.entity.User;
import com.turfreservation.exception.BadRequestException;
import com.turfreservation.exception.ResourceNotFoundException;
import com.turfreservation.exception.UnauthorizedException;
import com.turfreservation.repository.TurfRepository;
import com.turfreservation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurfService {

    @Autowired
    private TurfRepository turfRepository;

    @Autowired
    private UserRepository userRepository;

    public TurfResponse addTurf(Long ownerId, TurfRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        if (owner.getRole() != User.Role.TURF_OWNER) {
            throw new UnauthorizedException("Only Turf Owners can add turfs");
        }

        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new BadRequestException("Closing time must be after opening time");
        }

        Turf turf = new Turf();
        turf.setTurfName(request.getTurfName());
        turf.setLocation(request.getLocation());
        turf.setAddress(request.getAddress());
        turf.setSportType(request.getSportType());
        turf.setDescription(request.getDescription());
        turf.setPricePerSlot(request.getPricePerSlot());
        turf.setSlotDuration(request.getSlotDuration());
        turf.setOpeningTime(request.getOpeningTime());
        turf.setClosingTime(request.getClosingTime());
        turf.setOwner(owner);

        Turf saved = turfRepository.save(turf);
        return toResponse(saved);
    }

    public List<TurfResponse> getAllTurfs() {
        return turfRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TurfResponse getTurfById(Long id) {
        Turf turf = turfRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turf not found with id: " + id));
        return toResponse(turf);
    }

    public List<TurfResponse> searchTurfs(String keyword) {
        List<Turf> turfs = turfRepository
                .findByTurfNameContainingIgnoreCaseOrLocationContainingIgnoreCase(keyword, keyword);
        return turfs.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TurfResponse> searchByLocation(String location) {
        List<Turf> turfs = turfRepository.findByLocationContainingIgnoreCase(location);
        return turfs.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<TurfResponse> getOwnerTurfs(Long ownerId) {
        return turfRepository.findByOwnerId(ownerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TurfResponse updateTurf(Long turfId, Long ownerId, TurfRequest request) {
        Turf turf = turfRepository.findById(turfId)
                .orElseThrow(() -> new ResourceNotFoundException("Turf not found"));

        if (!turf.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("You do not own this turf");
        }

        if (!request.getClosingTime().isAfter(request.getOpeningTime())) {
            throw new BadRequestException("Closing time must be after opening time");
        }

        turf.setTurfName(request.getTurfName());
        turf.setLocation(request.getLocation());
        turf.setAddress(request.getAddress());
        turf.setSportType(request.getSportType());
        turf.setDescription(request.getDescription());
        turf.setPricePerSlot(request.getPricePerSlot());
        turf.setSlotDuration(request.getSlotDuration());
        turf.setOpeningTime(request.getOpeningTime());
        turf.setClosingTime(request.getClosingTime());

        Turf updated = turfRepository.save(turf);
        return toResponse(updated);
    }

    public Turf getTurfEntityById(Long id) {
        return turfRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turf not found with id: " + id));
    }

    private TurfResponse toResponse(Turf turf) {
        return new TurfResponse(
                turf.getId(),
                turf.getTurfName(),
                turf.getLocation(),
                turf.getAddress(),
                turf.getSportType(),
                turf.getDescription(),
                turf.getPricePerSlot(),
                turf.getSlotDuration(),
                turf.getOpeningTime(),
                turf.getClosingTime(),
                turf.getOwner().getId(),
                turf.getOwner().getName()
        );
    }
}