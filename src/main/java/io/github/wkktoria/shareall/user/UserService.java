package io.github.wkktoria.shareall.user;

import org.springframework.stereotype.Service;

@Service
class UserService {
    private final UserRepository userRepository;

    UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    User save(final User user) {
        return userRepository.save(user);
    }
}
