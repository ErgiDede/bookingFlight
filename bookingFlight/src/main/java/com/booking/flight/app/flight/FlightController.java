package com.booking.flight.app.flight;


import com.booking.flight.app.shared.objects.MessageJson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
@Slf4j
public class FlightController {

    private final FlightService flightService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @PostMapping
    public ResponseEntity<?> createFlight(@Valid @RequestBody CreateFlightRequest createFlightRequest) {
        logger.info("Flight created with flightNumber: " + createFlightRequest.getFlightNumber());
        flightService.createFlight(createFlightRequest);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new MessageJson("Flight is created successfully."));
    }

 }