package com.booking.flight.app.response;

import com.booking.flight.app.shared.enums.BookingEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {

    private Long id;
    private String airlineCode;
    private String flightNumber;
    private String origin;
    private String destination;
    private Date departureDate;
    private Date arrivalDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private BookingEnum bookingClasses;

}
