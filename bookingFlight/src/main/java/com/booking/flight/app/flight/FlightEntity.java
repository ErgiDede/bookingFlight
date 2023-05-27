package com.booking.flight.app.flight;


import com.booking.flight.app.shared.enums.BookingEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "flight")
public class FlightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "airlineCode", nullable = false)
    private String airlineCode;
    @Column(name = "flightNumber", nullable = false, unique = true)
    private String flightNumber;
    @Column(name = "origin", nullable = false)
    private String origin;
    @Column(name = "destination", nullable = false)
    private String destination;
    @Column(name = "departuresDate", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date departureDate;
    @Column(name = "arrivalDate", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date arrivalDate;
    @Column(name = "departureTime", nullable = false)
    private LocalTime departureTime;
    @Column(name = "arrivalTime", nullable = false)
    private LocalTime arrivalTime;
    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "totalSeats", nullable = false)
    private Integer totalSeats;

    @Column(name = "availableSeats", nullable = false)
    private Integer availableSeats;

    @Column(name = "classes", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private BookingEnum bookingClasses;

    @OneToMany(mappedBy = "flightEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<BookingFlight> bookingFlights;

    @Transient
    public Boolean isBooked(){
        return !Objects.equals(totalSeats, availableSeats);
    }

    @Transient
    public Boolean isFullyBooked(){
        return availableSeats == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FlightEntity flight = (FlightEntity) o;
        return getId() != null && Objects.equals(getId(), flight.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

