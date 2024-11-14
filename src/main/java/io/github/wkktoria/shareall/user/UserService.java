package io.github.wkktoria.shareall.user;

import io.github.wkktoria.shareall.error.NotFoundException;
import io.github.wkktoria.shareall.file.FileService;
import io.github.wkktoria.shareall.user.exception.DuplicateUsernameException;
import io.github.wkktoria.shareall.user.viewmodel.UserUpdateViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    public UserService(final UserRepository userRepository,
                       final PasswordEncoder passwordEncoder,
                       final FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
    }

    public User save(final User user) {
        User inDbUser = userRepository.findByUsername(user.getUsername());

        if (inDbUser != null) {
            throw new DuplicateUsernameException();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Page<User> getUsers(User loggedInUser, final Pageable pageable) {
        if (loggedInUser != null) {
            return userRepository.findByUsernameNot(loggedInUser.getUsername(), pageable);
        }
        return userRepository.findAll(pageable);
    }

    public User getByUsername(final String username) {
        User inDbUser = userRepository.findByUsername(username);
        if (inDbUser == null) {
            throw new NotFoundException(username + " not found");
        }
        return inDbUser;
    }

    public User update(final long id, UserUpdateViewModel userUpdate) {
        User inDbUser = userRepository.getReferenceById(id);
        inDbUser.setDisplayName(userUpdate.getDisplayName());

        if (userUpdate.getImage() != null) {
            String savedImageName;
            try {
                savedImageName = fileService.saveProfileImage(userUpdate.getImage());
                fileService.deleteProfileImage(inDbUser.getImage());
                inDbUser.setImage(savedImageName);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        return userRepository.save(inDbUser);
    }
}
