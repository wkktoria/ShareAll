package io.github.wkktoria.shareall;

import io.github.wkktoria.shareall.user.User;

public class TestUtil {
    public static User createValidUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setDisplayName("test-display");
        user.setPassword("P4sW@ord");
        user.setImage("profile-image.png");

        return user;
    }

    public static User createValidUser(final String username) {
        User user = createValidUser();
        user.setUsername(username);
        return user;
    }
}
