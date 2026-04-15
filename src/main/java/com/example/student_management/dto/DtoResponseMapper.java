package com.example.student_management.dto;

/**
 * Functional interface for mapping a {@link GenericStudentDto} back into a typed response
 * DTO {@code T}.
 *
 * <p>A lambda or method reference assigned to this interface receives a populated
 * {@link GenericStudentDto} and produces the concrete response object.  Because the
 * response DTO classes are untouched, the JSON shapes returned to the frontend remain
 * identical.
 *
 * <p>Example usage:
 * <pre>{@code
 * DtoResponseMapper<StudentProfileResponse> mapper =
 *     dto -> StudentProfileResponse.builder()
 *         .studentId(dto.getStudentId())
 *         // ... other fields ...
 *         .build();
 *
 * StudentProfileResponse response = mapper.toResponse(genericDto);
 * }</pre>
 *
 * @param <T> the concrete response DTO type
 */
@FunctionalInterface
public interface DtoResponseMapper<T> {

    /**
     * Converts the given {@link GenericStudentDto} into a typed response DTO.
     *
     * @param dto the populated generic DTO; must not be {@code null}
     * @return the concrete response DTO built from the generic DTO's fields
     */
    T toResponse(GenericStudentDto dto);
}
