package io.github.wkktoria.shareall.user.login;

import io.github.wkktoria.shareall.shared.CurrentUser;
import io.github.wkktoria.shareall.user.User;
import io.github.wkktoria.shareall.user.viewmodel.UserViewModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LoginController {
    @PostMapping("/api/1.0/login")
    UserViewModel handleLogin(@CurrentUser User loggedInUser) {
        return new UserViewModel(loggedInUser);
    }
}
