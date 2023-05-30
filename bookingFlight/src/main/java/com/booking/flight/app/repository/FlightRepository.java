package com.booking.flight.app.repository;

import com.booking.flight.app.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface FlightRepository extends JpaRepository<FlightEntity,Long> {

    List<FlightEntity> findByOriginAndDestinationAndDepartureDate(String origin, String destination, Date flightDate);

    List<FlightEntity> findByOriginAndDestinationAndDepartureDateAndAirlineCode(String origin, String destination, Date flightDate, String airlineCode);

}
