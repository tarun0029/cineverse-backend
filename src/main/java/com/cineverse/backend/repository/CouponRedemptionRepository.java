package com.cineverse.backend.repository;

import com.cineverse.backend.entity.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {

    long countByCouponIdAndUserId(Long couponId, Integer userId);

    boolean existsByCouponIdAndBookingId(Long couponId, UUID bookingId);
}
