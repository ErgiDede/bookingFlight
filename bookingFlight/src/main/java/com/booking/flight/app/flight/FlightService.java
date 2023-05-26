package com.booking.flight.app.flight;


import com.booking.flight.app.shared.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    public void createFlight(CreateFlightRequest createFlightRequest) {

        flightRepository.save(ModelMapperUtils.map(createFlightRequest, FlightEntity.class));

    }

}
