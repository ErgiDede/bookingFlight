package com.booking.flight.app.booking;

import com.booking.flight.app.flight.FlightResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private List<FlightResponse> flightResponses;
    private boolean isCancelled;
    private String cancellationReason;
    private Date bookingDate;

}
