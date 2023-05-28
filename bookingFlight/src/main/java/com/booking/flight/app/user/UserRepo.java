package com.booking.flight.app.user;


import com.booking.flight.app.shared.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameAndStatus(String username, UserStatus userStatus);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByUsernameIn(List<String> usernames);
}
