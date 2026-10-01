package com.turfreservation.repository;

import com.turfreservation.entity.TurfDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TurfDiscountRepository extends JpaRepository<TurfDiscount, Long> {

    List<TurfDiscount> findByTurfIdOrderByDiscountDateAscStartTimeAsc(Long turfId);

    List<TurfDiscount> findByTurfIdAndDiscountDate(Long turfId, LocalDate discountDate);
}