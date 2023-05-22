package com.booking.flight.app.configs.jwt;

import com.booking.flight.app.shared.helper.ContextHelper;
import com.booking.flight.app.shared.objects.ErrorMessageJson;
import com.booking.flight.app.shared.utils.JsonMapperUtils;
import com.booking.flight.app.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authorizationHeader = request.getHeader("Authorization");
            final JwtUtils jwtUtils = ContextHelper.getApplicationContext().getBean(JwtUtils.class);

            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtils.getSubject(jwt);
            }


            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = ContextHelper.getApplicationContext().getBean(UserService.class).findByActiveUsername(username);

                if (jwtUtils.validateToken(jwt)) {

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(JsonMapperUtils.convertObjectToJson(new ErrorMessageJson(HttpStatus.UNAUTHORIZED.value(),"Session expired!")));
        }
    }
}
