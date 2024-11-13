package io.github.wkktoria.shareall;

import io.github.wkktoria.shareall.user.User;
import io.github.wkktoria.shareall.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.stream.IntStream;

@SpringBootApplication
public class ShareallApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShareallApplication.class, args);
    }

    @Bean
    @Profile("dev")
    CommandLineRunner run(final UserService userService) {
        return args -> IntStream.rangeClosed(1, 15)
                .mapToObj(i -> {
                    User user = new User();
                    user.setUsername("user" + i);
                    user.setDisplayName("display" + i);
                    user.setPassword("P4sW@ord");
                    return user;
                }).forEach(userService::save);
    }
}
