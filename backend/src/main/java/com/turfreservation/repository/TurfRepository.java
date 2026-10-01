package com.turfreservation.repository;

import com.turfreservation.entity.Turf;
import com.turfreservation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurfRepository extends JpaRepository<Turf, Long> {

    List<Turf> findByOwner(User owner);

    List<Turf> findByOwnerId(Long ownerId);

    List<Turf> findByTurfNameContainingIgnoreCase(String turfName);

    List<Turf> findByLocationContainingIgnoreCase(String location);

    List<Turf> findByTurfNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String turfName, String location);
}