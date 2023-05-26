package com.booking.flight.app.dto;

import com.booking.flight.app.flight.FlightEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private List<FlightEntity> flightEntities;
    private boolean isCancelled;
    private String cancellationReason;
}
