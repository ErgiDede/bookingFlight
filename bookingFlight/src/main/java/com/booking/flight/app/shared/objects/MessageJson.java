package com.booking.flight.app.shared.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageJson {
    @JsonProperty("message")
    private String message;
}
