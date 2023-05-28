package com.booking.flight.app.booking;

import com.booking.flight.app.flight.BookingFlight;
import com.booking.flight.app.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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
    @ToString.Exclude
    private UserEntity userEntity;

    @Column(name = "bookingDate", nullable = false)
    @Temporal(value = TemporalType.DATE)
    private Date bookingDate;

    @OneToMany(mappedBy = "bookingEntity", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<BookingFlight> bookingFlights;

    @Column(name = "cancellationRequested")
    private Boolean cancellationRequested;

    @Column(name = "cancellationApproval")
    private Boolean cancellationApproval;

    @Column(name = "cancellationDeclineReason")
    private String cancellationDeclineReason;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BookingEntity booking = (BookingEntity) o;
        return getId() != null && Objects.equals(getId(), booking.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
