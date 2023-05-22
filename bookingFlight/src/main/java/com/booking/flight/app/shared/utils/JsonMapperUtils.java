package com.booking.flight.app.shared.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapperUtils {

    public static String convertObjectToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "Failed to convert Object to JSON.";
        }
    }

    public static <T> T convertJsonToObject(String json, Class<T> destClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, destClass);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
