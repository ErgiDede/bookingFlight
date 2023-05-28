package com.booking.flight.app.auth;


import com.booking.flight.app.configs.jwt.JwtUtils;

import com.booking.flight.app.shared.exceptions.ForbiddenException;
import com.booking.flight.app.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    public final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userService.findByActiveUsername(request.getUsername());
        var jwtToken = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            throw new ForbiddenException("Session Expired.");
        }
        refreshToken = authHeader.substring(7);
        username = jwtUtils.getSubject(refreshToken);
        if (username != null) {
            var user = userService.findByActiveUsername(username);
            if (jwtUtils.validateToken(refreshToken)) {
                var accessToken = jwtUtils.generateToken(user);
                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new ForbiddenException("Session Expired.");
    }
}
