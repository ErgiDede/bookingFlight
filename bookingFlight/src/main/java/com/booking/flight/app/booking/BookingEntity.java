package com.booking.flight.app.booking;

import com.booking.flight.app.flight.BookingFlight;
import com.booking.flight.app.flight.FlightEntity;
import com.booking.flight.app.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booking")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "isCanceled")
    private boolean isCancelled;

    @Column(name = "cancellationReason")
    private String cancellationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(name = "bookingDate", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date bookingDate;

    @OneToMany(mappedBy = "bookingEntity", cascade = CascadeType.ALL)
    private List<BookingFlight> bookingFlights;

    @Column(name = "cancellationRequested")
    private Boolean cancellationRequested;

    @Column(name = "cancellationApproval")
    private Boolean cancellationApproval;

    @Column(name = "cancellationDeclineReason")
    private String cancellationDeclineReason;

}
