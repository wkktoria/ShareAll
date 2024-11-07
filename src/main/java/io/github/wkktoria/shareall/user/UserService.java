package io.github.wkktoria.shareall.user;

import io.github.wkktoria.shareall.user.exception.DuplicateUsernameException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    UserService(final UserRepository userRepository,
                final BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    User save(final User user) {
        User inDbUser = userRepository.findByUsername(user.getUsername());

        if (inDbUser != null) {
            throw new DuplicateUsernameException();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
