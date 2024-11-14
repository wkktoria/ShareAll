package io.github.wkktoria.shareall.user;

import io.github.wkktoria.shareall.error.ApiError;
import io.github.wkktoria.shareall.shared.CurrentUser;
import io.github.wkktoria.shareall.shared.GenericResponse;
import io.github.wkktoria.shareall.user.viewmodel.UserUpdateViewModel;
import io.github.wkktoria.shareall.user.viewmodel.UserViewModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/1.0")
class UserController {
    private final UserService userService;

    UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    GenericResponse createUser(@Valid @RequestBody final User user) {
        userService.save(user);
        return new GenericResponse("User saved successfully");
    }

    @GetMapping("/users")
    Page<UserViewModel> getUsers(@CurrentUser User loggedInUser, final Pageable pageable) {
        return userService.getUsers(loggedInUser, pageable).map(UserViewModel::new);
    }

    @GetMapping("/users/{username}")
    UserViewModel getUserByUsername(@PathVariable final String username) {
        User user = userService.getByUsername(username);
        return new UserViewModel(user);
    }

    @PutMapping("/users/{id:[0-9]+}")
    @PreAuthorize("#id == principal.id")
    UserViewModel updateUser(@PathVariable final long id, @Valid @RequestBody(required = false) UserUpdateViewModel userUpdate) {
        User updated = userService.update(id, userUpdate);
        return new UserViewModel(updated);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleValidationException(MethodArgumentNotValidException exception,
                                       HttpServletRequest request) {
        ApiError apiError = new ApiError(400, "Validation error", request.getServletPath());

        BindingResult result = exception.getBindingResult();
        Map<String, String> validationErrors = new HashMap<>();

        for (FieldError fieldError : result.getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        apiError.setValidationErrors(validationErrors);

        return apiError;
    }
}
