package io.github.wkktoria.shareall.user.login;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.wkktoria.shareall.shared.CurrentUser;
import io.github.wkktoria.shareall.user.User;
import io.github.wkktoria.shareall.user.views.Views;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LoginController {
    @PostMapping("/api/1.0/login")
    @JsonView(Views.Base.class)
    User handleLogin(@CurrentUser User loggedInUser) {
        return loggedInUser;
    }
}
