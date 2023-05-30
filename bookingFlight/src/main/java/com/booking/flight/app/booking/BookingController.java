package com.booking.flight.app.booking;

import com.booking.flight.app.shared.objects.MessageJson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PreAuthorize("hasRole('TRAVELLER')")
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody CreateBookingRequest request) {
            logger.info("Booking created successfully");
            bookingService.createBooking(request);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new MessageJson("Booking is created successfully."));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public List<BookingResponse> getBookingsByUserId(@PathVariable Long userId) {
        return bookingService.getBookingsByUserId(userId);
    }

    @PreAuthorize("hasRole('TRAVELLER')")
    @GetMapping("/myBookings")
    public List<BookingResponse> getMyBookings(@RequestParam(defaultValue = "0") int pageNumber) {
        return bookingService.getAllBookingsForLoggedInUser(pageNumber, 5);
    }

    @PreAuthorize("hasRole('TRAVELLER')")
    @PutMapping("/{bookingId}/canceling")
    public ResponseEntity<String> requestCancellation(@PathVariable Long bookingId, @RequestParam String cancellationReason) {
        logger.info("Cancellation request sent successfully for booking with id : " + bookingId);
        bookingService.requestCancellation(bookingId, cancellationReason);
        return ResponseEntity.ok("Cancellation request sent successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{bookingId}/cancel/approve")
    public ResponseEntity<String> approveCancellation(@PathVariable Long bookingId) {
        logger.info("Cancellation approved successfully for booking with id: " + bookingId);
        bookingService.approveCancellation(bookingId);
        return ResponseEntity.ok("Cancellation approved successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{bookingId}/cancel/decline")
    public ResponseEntity<String> declineCancellation(@PathVariable Long bookingId, @RequestParam String declineReason) {
        logger.info("Cancellation  declined for booking with id: " + bookingId);

        bookingService.declineCancellation(bookingId, declineReason);
        return ResponseEntity.ok("Cancellation declined.");
    }

}

