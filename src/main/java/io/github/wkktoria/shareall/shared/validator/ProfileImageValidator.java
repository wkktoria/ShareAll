package io.github.wkktoria.shareall.shared.validator;

import io.github.wkktoria.shareall.file.FileService;
import io.github.wkktoria.shareall.shared.annotation.ProfileImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Base64;

public class ProfileImageValidator implements ConstraintValidator<ProfileImage, String> {
    private final FileService fileService;

    public ProfileImageValidator(final FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(value);
        String fileType = fileService.detectType(decodedBytes);

        return fileType.equalsIgnoreCase("image/png") || fileType.equalsIgnoreCase("image/jpeg");
    }
}
