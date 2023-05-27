package com.booking.flight.app.booking;

import com.booking.flight.app.flight.BookingFlight;
import com.booking.flight.app.flight.FlightEntity;
import com.booking.flight.app.flight.FlightRepository;
import com.booking.flight.app.flight.FlightResponse;
import com.booking.flight.app.loadData.LoadData;
import com.booking.flight.app.shared.utils.ModelMapperUtils;
import com.booking.flight.app.user.UserDto;
import com.booking.flight.app.user.UserEntity;
import com.booking.flight.app.user.UserRepo;
import com.booking.flight.app.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void createBooking(CreateBookingRequest createBookingRequest) {

        List<Long> flightIds = createBookingRequest.getFlightIds();
        List<FlightEntity> flights = flightRepository.findAllById(flightIds);

        // Check if all flights exist
        if (flights.size() != flightIds.size()) {
            throw new IllegalArgumentException("One or more flights not found");
        }
        UserDto userDto = userService.getLoggedInUser();

        UserEntity user = ModelMapperUtils.map(userDto, UserEntity.class);

        // Check availability for all flights
        for (FlightEntity flight : flights) {
            if (flight.isFullyBooked()) {
                throw new IllegalStateException("No available seats for flight: " + flight.getId());
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
        }
        else {
            throw new IllegalArgumentException("Flight departure date must be in the future");
        }

        flightRepository.saveAll(flights);

    }

    public List<BookingResponse> getAllBookings(Long userId) {
        List<BookingEntity> bookingEntities = bookingRepository.findByUserEntityId(userId); // Replace "bookingRepository" with your actual repository

        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (BookingEntity bookingEntity : bookingEntities) {
            BookingResponse bookingResponse = ModelMapperUtils.map(bookingEntity, BookingResponse.class);
            bookingResponse.setFlightResponses(mapFlightEntitiesToResponses(bookingEntity.getBookingFlights()));
            bookingResponses.add(bookingResponse);
        }

        return bookingResponses;
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
        BookingEntity booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking != null) {
            booking.setCancellationRequested(true);
            booking.setCancellationReason(cancellationReason);
            bookingRepository.save(booking);
        }
    }

    public void approveCancellation(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking != null) {
            booking.setCancelled(true);
            booking.setCancellationRequested(false);
            // Update available seats for the flights if necessary
            updateAvailableSeats(booking);
            bookingRepository.save(booking);
        }
    }

    public void declineCancellation(Long bookingId, String declineReason) {
        BookingEntity booking = bookingRepository.findById(bookingId).orElse(null);

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

    public List<BookingResponse> getAllBookingsForLogedInUser( int page, int size) {
        Long userId = getLogedIdUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingEntity> bookingPage = bookingRepository.findByUserEntityIdOrderByBookingDateDesc(userId, pageable);

        List<BookingEntity> bookingEntities = bookingPage.getContent();

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

    public Long getLogedIdUserId(){
        UserDto userDto = userService.getLoggedInUser();
        return userDto.getId();
    }
}
