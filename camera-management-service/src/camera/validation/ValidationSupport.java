package camera.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public final class ValidationSupport {
    private static final Validator VALIDATOR = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    private ValidationSupport() {
    }

    public static <T> void validate(T value) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(value);
        throwIfInvalid(violations);
    }

    public static <T> void validateValue(Class<T> beanType, String propertyName, Object value) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validateValue(beanType, propertyName, value);
        throwIfInvalid(violations);
    }

    public static void validateRequired(String name, Object value) {
        try {
            validate(new RequiredValue(value));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(name + " must not be null.", exception);
        }
    }

    public static void validateNotBlank(String name, String value) {
        try {
            validate(new RequiredText(value));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(name + " must not be blank.", exception);
        }
    }

    private static <T> void throwIfInvalid(Set<ConstraintViolation<T>> violations) {
        if (violations.isEmpty()) {
            return;
        }

        String message = violations.stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.joining("; "));

        throw new IllegalArgumentException(message);
    }

    private record RequiredValue(@NotNull Object value) {
    }

    private record RequiredText(@NotBlank String value) {
    }
}
