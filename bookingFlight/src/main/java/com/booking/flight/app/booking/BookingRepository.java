package com.booking.flight.app.booking;

import com.booking.flight.app.flight.FlightEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByUserEntityId(Long userID);

    Page<BookingEntity> findByUserEntityIdOrderByBookingDateDesc(Long userID, Pageable pageable);

}
