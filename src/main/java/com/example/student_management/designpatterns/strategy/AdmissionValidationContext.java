package com.example.student_management.designpatterns.strategy;

import com.example.student_management.dto.AdmissionDetailsRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AdmissionValidationContext {

    private final Map<String, AdmissionValidationStrategy> strategyMap;

    public AdmissionValidationContext(List<AdmissionValidationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.supportedPattern().toLowerCase(),
                        Function.identity()));
    }

    public void validate(AdmissionDetailsRequest request) {
        String pattern = request.getAdmissionPattern().toLowerCase();
        AdmissionValidationStrategy strategy = strategyMap.get(pattern);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported admission pattern: " + request.getAdmissionPattern());
        }
        strategy.validate(request);
    }
}

