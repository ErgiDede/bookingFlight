package com.booking.flight.app.user;

import com.booking.flight.app.shared.objects.MessageJson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable(value = "email") String email) {
        logger.info("Getting user with email:" + email);
        return ResponseEntity.ok().body(userService.getByEmail(email));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        logger.info("Creating user with username: " + createUserRequest.getUsername());
        return ResponseEntity.ok().body(userService.createUser(createUserRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        logger.info("Updating user with id:" + updateUserRequest.getId());
        return ResponseEntity.ok().body(userService.updateUser(updateUserRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        logger.info("Getting all users");
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") long id) {
        logger.info("Deleting user with id: " + id);
        userService.deleteUser(id);
        return ResponseEntity.ok().body(new MessageJson("User is deleted successfully."));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/flight/{flightId}")
    public List<UserResponse> travellersWhoHaveBookedOnASpecifiedFlight(@PathVariable Long flightId) {
        return userService.getTravellersByFlight(flightId);
    }

}