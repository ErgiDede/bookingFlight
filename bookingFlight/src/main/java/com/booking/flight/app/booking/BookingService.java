package com.booking.flight.app.booking;

import com.booking.flight.app.flight.BookingFlight;
import com.booking.flight.app.flight.FlightEntity;
import com.booking.flight.app.flight.FlightRepository;
import com.booking.flight.app.flight.FlightResponse;
import com.booking.flight.app.shared.utils.ModelMapperUtils;
import com.booking.flight.app.user.UserEntity;
import com.booking.flight.app.user.UserResponse;
import com.booking.flight.app.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    public void createBooking(CreateBookingRequest createBookingRequest) throws IllegalArgumentException {
        List<Long> flightIds = createBookingRequest.getFlightIds();
        List<FlightEntity> flights = flightRepository.findAllById(flightIds);

        // Check if all flights exist
        if (flights.isEmpty()) {
            throw new IllegalArgumentException("One or more flights not found");
        }

        UserResponse userResponse = userService.getLoggedInUser();
        UserEntity user = ModelMapperUtils.map(userResponse, UserEntity.class);

        // Check availability for all flights
        for (FlightEntity flight : flights) {
            if (Boolean.TRUE.equals(flight.isFullyBooked())) {
                throw new IllegalArgumentException("No available seats for flight: " + flight.getId());
            }
        }

        // Create a new booking entity
        BookingEntity booking = new BookingEntity();
        booking.setUserEntity(user);
        booking.setCancelled(false);
        booking.setBookingDate(Calendar.getInstance().getTime());

        List<BookingFlight> bookingFlights = new ArrayList<>();
        for (FlightEntity flight : flights) {
            BookingFlight bookingFlight = new BookingFlight();
            bookingFlight.setFlightEntity(flight);
            bookingFlight.setBookingEntity(booking);
            bookingFlights.add(bookingFlight);

            // Update available seats count
            flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        }

        booking.setBookingFlights(bookingFlights);

        boolean isFlightDateValid = checkFlightDate(booking);
        if (isFlightDateValid) {
            bookingRepository.save(booking);
        } else {
            throw new IllegalArgumentException("Flight departure date must be in the future");
        }

        flightRepository.saveAll(flights);

    }


    public List<BookingResponse> getBookingsByUserId(Long userId) {
        List<BookingEntity> bookingEntities = bookingRepository.findByUserEntityId(userId); // Replace "bookingRepository" with your actual repository

        return getBookingResponses(bookingEntities);
    }

    private List<FlightResponse> mapFlightEntitiesToResponses(List<BookingFlight> bookingFlights) {
        List<FlightResponse> flightResponses = new ArrayList<>();
        for (BookingFlight bookingFlight : bookingFlights) {
            FlightEntity flightEntity = bookingFlight.getFlightEntity();
            flightResponses.add(ModelMapperUtils.map(flightEntity, FlightResponse.class));
        }
        return flightResponses;
    }

    public void requestCancellation(Long bookingId, String cancellationReason) {
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(EntityNotFoundException::new);

        if (booking != null) {
            booking.setCancellationRequested(true);
            booking.setCancellationReason(cancellationReason);
            bookingRepository.save(booking);
        }
    }

    public void approveCancellation(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(EntityNotFoundException::new);

        if (booking != null) {
            booking.setCancelled(true);
            booking.setCancellationRequested(false);
            updateAvailableSeats(booking);
            bookingRepository.save(booking);
        }
    }

    public void declineCancellation(Long bookingId, String declineReason) {
        BookingEntity booking = bookingRepository.findById(bookingId).orElseThrow(EntityNotFoundException::new);

        if (booking != null && booking.getCancellationRequested()) {
            booking.setCancellationRequested(false);
            booking.setCancellationDeclineReason(declineReason);
            bookingRepository.save(booking);
        }
    }

    private void updateAvailableSeats(BookingEntity booking) {
        List<BookingFlight> bookingFlights = booking.getBookingFlights();

        for (BookingFlight bookingFlight : bookingFlights) {
            FlightEntity flight = bookingFlight.getFlightEntity();
            flight.setAvailableSeats(flight.getAvailableSeats() + 1);
            flightRepository.save(flight);
        }
    }

    public List<BookingResponse> getAllBookingsForLoggedInUser(int page, int size) {
        Long userId = getLoggedIdUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingEntity> bookingPage = bookingRepository.findByUserEntityIdOrderByBookingDateDesc(userId, pageable);

        List<BookingEntity> bookingEntities = bookingPage.getContent();

        return getBookingResponses(bookingEntities);
    }

    private List<BookingResponse> getBookingResponses(List<BookingEntity> bookingEntities) {
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (BookingEntity bookingEntity : bookingEntities) {
            BookingResponse bookingResponse = ModelMapperUtils.map(bookingEntity, BookingResponse.class);
            bookingResponse.setFlightResponses(mapFlightEntitiesToResponses(bookingEntity.getBookingFlights()));
            bookingResponses.add(bookingResponse);
        }
        return bookingResponses;
    }

    private boolean checkFlightDate(BookingEntity booking) {
        List<BookingFlight> bookingFlights = booking.getBookingFlights();

        for (BookingFlight bookingFlight : bookingFlights) {
            FlightEntity flight = bookingFlight.getFlightEntity();
            Date departureDate = flight.getDepartureDate();

            if (departureDate != null && departureDate.before(new Date())) {
                return false; // Flight departure date is in the past
            }
        }
        return true; // All flight departure dates are in the future
    }

    public Long getLoggedIdUserId() {
        return userService.getLoggedInUser().getId();
    }
}
