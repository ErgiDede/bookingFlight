package com.booking.flight.app.booking;

import com.booking.flight.app.shared.objects.MessageJson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody CreateBookingRequest request) {
        try {
            bookingService.createBooking(request);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new MessageJson("Booking is created successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("{userId}")
    public List<BookingResponse> bookingEntities(@PathVariable Long userId){
        return bookingService.getAllBookings(userId);
    }

    @GetMapping("/myBookings")
    public List<BookingResponse> getMyBookings(@RequestParam(defaultValue = "0") int pageNumber){
        return bookingService.getAllBookingsForLogedInUser(pageNumber,5);
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<String> requestCancellation(@PathVariable Long bookingId, @RequestParam String cancellationReason) {
        bookingService.requestCancellation(bookingId, cancellationReason);
        return ResponseEntity.ok("Cancellation request sent successfully");
    }

    @PutMapping("/{bookingId}/cancel/approve")
    public ResponseEntity<String> approveCancellation(@PathVariable Long bookingId) {
        bookingService.approveCancellation(bookingId);
        return ResponseEntity.ok("Cancellation approved successfully");
    }

    @PutMapping("/{bookingId}/cancel/decline")
    public ResponseEntity<String> declineCancellation(@PathVariable Long bookingId, @RequestParam String declineReason) {
        bookingService.declineCancellation(bookingId, declineReason);
        return ResponseEntity.ok("Cancellation declined successfully");
    }

}

