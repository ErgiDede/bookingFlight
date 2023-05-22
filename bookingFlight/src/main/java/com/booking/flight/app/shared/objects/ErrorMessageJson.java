package com.booking.flight.app.shared.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessageJson {
    @JsonProperty("code")
    private int errorCode;
    @JsonProperty("message")
    private String message;
}
