package camera.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = CameraSetValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface ValidCameraSet {
    String message() default "camera ids, names, and connection urls must be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
