package com.booking.flight.app.user;


import com.booking.flight.app.shared.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndStatus(String username, UserStatus userStatus);
    Optional<User> findByUsername(String username);
}
