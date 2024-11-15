package io.github.wkktoria.shareall.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.wkktoria.shareall.shared.GenericResponse;
import io.github.wkktoria.shareall.shared.annotation.CurrentUser;
import io.github.wkktoria.shareall.user.viewmodel.UserUpdateViewModel;
import io.github.wkktoria.shareall.user.viewmodel.UserViewModel;
import jakarta.validation.Valid;

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
	UserViewModel updateUser(@PathVariable final long id,
			@Valid @RequestBody(required = false) UserUpdateViewModel userUpdate) {
		User updated = userService.update(id, userUpdate);
		return new UserViewModel(updated);
	}
}
