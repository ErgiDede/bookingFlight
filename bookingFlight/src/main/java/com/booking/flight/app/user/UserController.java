package com.booking.flight.app.user;

import com.booking.flight.app.shared.objects.MessageJson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * This endpoint is called when a user is created by ADMIN
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Creating user with username: " + userDto.getUsername());
        return ResponseEntity.ok().body(userService.createUser(userDto));
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Updating user with id:" + userDto.getId());
        return ResponseEntity.ok().body(userService.updateUser(userDto));
    }

  /*  @PutMapping("{id}")
    public ResponseEntity<?> softDeleteUser(@PathVariable(value = "id") long id) {
        logger.info("Deactivating user with id:" + id);
        return ResponseEntity.ok().body(userService.deactivateUser(id));
    }*/

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        logger.info("Getting all users");
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable(value = "email") String email) {
        logger.info("Getting user with email:" + email);
        return ResponseEntity.ok().body(userService.getByEmail(email));

    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") long id) {
        logger.info("Deleting user with id:" + id);
        userService.deleteUser(id);
        return ResponseEntity.ok().body(new MessageJson("User is deleted successfully."));
    }

    @GetMapping("/loggedInUser")
    public ResponseEntity<?> getLoggedInUser() {
        return ResponseEntity.ok().body(userService.getLoggedInUser());
    }
}