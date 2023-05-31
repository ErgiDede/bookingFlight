package com.booking.flight.app;

import com.booking.flight.app.entity.BookingEntity;
import com.booking.flight.app.entity.FlightEntity;
import com.booking.flight.app.repository.BookingRepository;
import com.booking.flight.app.repository.FlightRepository;
import com.booking.flight.app.request.CreateBookingRequest;
import com.booking.flight.app.service.BookingService;
import com.booking.flight.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingService bookingService;


    @Test
    void createBooking_FlightNotFound_ExceptionThrown() {
        // Mock data
        CreateBookingRequest createBookingRequest = new CreateBookingRequest();
        List<Long> flightIds = List.of(1L, 2L);
        createBookingRequest.setFlightIds(flightIds);

        // Mock behavior
        when(flightRepository.findAllById(flightIds)).thenReturn(new ArrayList<>());

        // Invoke the method under test
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(createBookingRequest));

        // Verify the interactions
        verify(bookingRepository, never()).save(any(BookingEntity.class));
        verify(flightRepository, never()).save(any(FlightEntity.class));
    }
}

