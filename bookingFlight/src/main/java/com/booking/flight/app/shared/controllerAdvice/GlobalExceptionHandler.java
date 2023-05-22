package com.booking.flight.app.shared.controllerAdvice;

import com.booking.flight.app.shared.exceptions.BadRequestException;
import com.booking.flight.app.shared.exceptions.ForbiddenException;
import com.booking.flight.app.shared.exceptions.UserAlreadyExistsException;
import com.booking.flight.app.shared.exceptions.UserNotFoundException;
import com.booking.flight.app.shared.objects.ErrorMessageJson;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class, UserAlreadyExistsException.class, UserNotFoundException.class,
            MethodArgumentNotValidException.class, IOException.class, ForbiddenException.class,
            ExpiredJwtException.class})
    public final ResponseEntity<?> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status;

        //this is for validation exceptions which means @Size, @Pattern, @Email etc
        if (ex instanceof MethodArgumentNotValidException e) {
            status = HttpStatus.BAD_REQUEST;
            return handleMethodArgumentNotValid(e, headers, status, request);
        }

        if (ex instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof UserAlreadyExistsException) {
            status = HttpStatus.CONFLICT;
        } else if (ex instanceof UserNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof IOException || ex instanceof ForbiddenException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof ExpiredJwtException) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }

        return handleCustomExceptions(ex, headers, status, request);
    }

    protected ResponseEntity<?> handleExceptionInternal(Exception ex, ErrorMessageJson body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(body, headers, status);
    }

    protected ResponseEntity<?> handleCustomExceptions(Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMessageJson errorMessage = new ErrorMessageJson(status.value(), ex.getMessage());
        return handleExceptionInternal(ex, errorMessage, headers, status, request);
    }

    //this is for validation exceptions which means @Size, @Pattern, @Email etc
    protected ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorMessageJson errorMessage = new ErrorMessageJson(status.value(), "");
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> errorMessage.setMessage(errorMessage.getMessage().concat(Objects.requireNonNull(error.getDefaultMessage()))));
        return handleExceptionInternal(ex, errorMessage, headers, status, request);
    }

}
