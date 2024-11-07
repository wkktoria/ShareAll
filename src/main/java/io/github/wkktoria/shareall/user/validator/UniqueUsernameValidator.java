package io.github.wkktoria.shareall.user.validator;

import io.github.wkktoria.shareall.user.User;
import io.github.wkktoria.shareall.user.UserRepository;
import io.github.wkktoria.shareall.user.annotation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        User inDbUser = userRepository.findByUsername(value);

        return inDbUser == null;
    }
}
