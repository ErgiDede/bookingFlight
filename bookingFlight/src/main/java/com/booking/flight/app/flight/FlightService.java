package com.booking.flight.app.flight;


import com.booking.flight.app.shared.utils.ModelMapperUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    public void createFlight(CreateFlightRequest createFlightRequest) {
        if (createFlightRequest.isOriginDifferentFromDestination()){
            throw new IllegalArgumentException("Origin should be different from destination. ");
        }
        flightRepository.save(ModelMapperUtils.map(createFlightRequest, FlightEntity.class));
    }

    public void updateFlight(UpdateFlightRequest updateFlightRequest, Long flightId) {
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(EntityNotFoundException::new);
        if (Boolean.TRUE.equals(flight.isBooked())) {
            flight.setDepartureTime(updateFlightRequest.getDepartureTime());
            flightRepository.save(flight);
        } else {
            ModelMapperUtils.map(updateFlightRequest, flight);
            flightRepository.save(flight);
        }
    }

    public void deleteFlight(Long flightId) {
        FlightEntity flight = flightRepository.findById(flightId)
                .orElseThrow(EntityNotFoundException::new);
        if (!Boolean.TRUE.equals(flight.isBooked())) {
            flightRepository.delete(flight);
        }else {
            throw new IllegalArgumentException("Booked flights can not be deleted");
        }

    }

    public List<FlightResponse> searchFlights(String origin, String destination, Date flightDate, String airlineCode) {
        List<FlightEntity> flightEntities;

        if (airlineCode != null && !airlineCode.isEmpty()) {
            flightEntities = flightRepository.findByOriginAndDestinationAndDepartureDateAndAirlineCode(origin, destination, flightDate, airlineCode);
        } else {
            flightEntities = flightRepository.findByOriginAndDestinationAndDepartureDate(origin, destination, flightDate);
        }

        List<FlightResponse> flightResponses = new ArrayList<>();
        for (FlightEntity flightEntity : flightEntities) {
            flightResponses.add(ModelMapperUtils.map(flightEntity, FlightResponse.class));
        }

        return flightResponses;
    }
}
