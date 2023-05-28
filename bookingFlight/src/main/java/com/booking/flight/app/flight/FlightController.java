package com.booking.flight.app.flight;


import com.booking.flight.app.shared.objects.MessageJson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
@Slf4j
public class FlightController {

    private final FlightService flightService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createFlight(@Valid @RequestBody CreateFlightRequest createFlightRequest) {
        logger.info("Flight created with flightNumber: " + createFlightRequest.getFlightNumber());
        flightService.createFlight(createFlightRequest);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new MessageJson("Flight is created successfully."));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<?> updateFlight(@Valid @RequestBody UpdateFlightRequest updateFlightRequest, @PathVariable(value = "id") long id) {
        logger.info("Flight updated successfully   with flightNumber: " + updateFlightRequest.getFlightNumber());
        flightService.updateFlight(updateFlightRequest, id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new MessageJson("Flight updated successfully."));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteFlight(@PathVariable(value = "id") long id) {
        logger.info("Flight deleted successfully   with id: " + id);
        flightService.deleteFlight(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new MessageJson("Flight deleted successfully."));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<List<FlightResponse>> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date flightDate,
            @RequestParam(required = false) String airlineCode
    ) {
        List<FlightResponse> flights = flightService.searchFlights(origin, destination, flightDate, airlineCode);
        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

}