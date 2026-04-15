package com.example.student_management.dto;

/**
 * Functional interface for mapping a typed request DTO {@code T} into a
 * {@link GenericStudentDto}.
 *
 * <p>A lambda or method reference assigned to this interface receives an incoming request
 * object and produces a fully-populated {@link GenericStudentDto} whose fields are sourced
 * from that request.  The original request DTO class is left unchanged so the public API
 * contract is preserved.
 *
 * <p>Example usage:
 * <pre>{@code
 * DtoRequestMapper<StudentUpsertRequest> mapper =
 *     request -> GenericStudentDto.builder()
 *         .firstName(request.getFirstName())
 *         // ... other fields ...
 *         .build();
 *
 * GenericStudentDto generic = mapper.fromRequest(someRequest);
 * }</pre>
 *
 * @param <T> the concrete request DTO type
 */
@FunctionalInterface
public interface DtoRequestMapper<T> {

    /**
     * Converts the given request DTO into a {@link GenericStudentDto}.
     *
     * @param request the incoming request DTO; must not be {@code null}
     * @return a populated {@link GenericStudentDto} containing the request data
     */
    GenericStudentDto fromRequest(T request);
}
