package com.example.student_management.designpatterns.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AdmissionDetailsFactoryProvider {

    private final Map<String, AdmissionDetailsFactory> factoryMap;

    public AdmissionDetailsFactoryProvider(List<AdmissionDetailsFactory> factories) {
        this.factoryMap = factories.stream()
                .collect(Collectors.toMap(
                        factory -> factory.supportedPattern().toLowerCase(),
                        Function.identity()));
    }

    public AdmissionDetailsFactory getFactory(String pattern) {
        AdmissionDetailsFactory factory = factoryMap.get(pattern.toLowerCase());
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported admission pattern: " + pattern);
        }
        return factory;
    }
}

