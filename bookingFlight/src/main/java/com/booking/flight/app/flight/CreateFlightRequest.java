package com.booking.flight.app.flight;

import com.booking.flight.app.shared.enums.BookingEnum;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlightRequest {

    private String airlineCode;
    private String flightNumber;
    private String origin;
    private String destination;
    private Date departureDate;
    private Date arrivalDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Double price;
    private Integer totalSeats;
    private Integer availableSeats;
    private BookingEnum bookingClasses;


    public boolean isOriginDifferentFromDestination() {
        return origin.equalsIgnoreCase(destination);
    }
}
