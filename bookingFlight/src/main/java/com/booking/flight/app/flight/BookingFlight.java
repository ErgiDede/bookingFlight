package com.booking.flight.app.flight;

import com.booking.flight.app.booking.BookingEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "booking_flight")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private BookingEntity bookingEntity;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private FlightEntity flightEntity;

}

