package com.booking.flight.app;

import com.booking.flight.app.entity.FlightEntity;
import com.booking.flight.app.repository.FlightRepository;
import com.booking.flight.app.request.CreateFlightRequest;
import com.booking.flight.app.service.FlightService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;


    @Test
    void createFlight_OriginSameAsDestination_ExceptionThrown() {
        // Mock data
        CreateFlightRequest createFlightRequest = new CreateFlightRequest();
        // Set the required properties of createFlightRequest
        createFlightRequest.setOrigin("CityA");
        createFlightRequest.setDestination("CityA");

        // Invoke the method under test
        assertThrows(IllegalArgumentException.class, () -> flightService.createFlight(createFlightRequest));

        // Verify the interactions
        verify(flightRepository, never()).save(any(FlightEntity.class));
    }



}

