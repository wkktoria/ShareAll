package io.github.wkktoria.shareall.user;

import io.github.wkktoria.shareall.shared.GenericResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {
    private final UserService userService;

    UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/1.0/users")
    GenericResponse createUser(@RequestBody final User user) {
        userService.save(user);
        return new GenericResponse("User saved successfully");
    }
}
