package camera.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.opencv.core.Size;

public class FrameSizeValidator implements ConstraintValidator<ValidFrameSize, Size> {
    @Override
    public boolean isValid(Size value, ConstraintValidatorContext context) {
        return value == null || value.width > 0 && value.height > 0;
    }
}
