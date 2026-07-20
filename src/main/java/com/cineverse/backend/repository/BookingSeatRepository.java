package com.cineverse.backend.repository;

import com.cineverse.backend.entity.BookingSeat;
import com.cineverse.backend.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    List<BookingSeat> findByBookingId(UUID bookingId);

    @Query("""
            select bs.seat.id from BookingSeat bs
            where bs.booking.show.id = :showId
              and bs.booking.status in :statuses
            """)
    List<Long> findBookedSeatIdsByShowIdAndStatuses(
            @Param("showId") UUID showId,
            @Param("statuses") Collection<BookingStatus> statuses);
}
