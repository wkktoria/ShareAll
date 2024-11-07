package io.github.wkktoria.shareall.user.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class LoginController {
    @PostMapping("/api/1.0/login")
    void handleLogin() {
        
    }
}
