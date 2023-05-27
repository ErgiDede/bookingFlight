package com.booking.flight.app.shared.utils;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Collection;
import java.util.List;


public class ModelMapperUtils {

    private static final ModelMapper modelMapper;

    static {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        registerNullFieldConverter();
    }

    private ModelMapperUtils() {
    }

    public static <D, T> D map(final T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public static <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outClass) {
        return entityList.stream()
                .map(entity -> map(entity, outClass))
                .toList();
    }

    public static <S, D> D map(final S source, D destination) {
        modelMapper.map(source, destination);
        return destination;
    }

    private static void registerNullFieldConverter() {
        Converter<Object, Object> nullFieldConverter = new AbstractConverter<>() {
            @Override
            protected Object convert(Object source) {
                return source; // Leave the field as it is
            }
        };

        modelMapper.addConverter(nullFieldConverter);
    }
}
