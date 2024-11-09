package io.github.wkktoria.shareall.user.viewmodel;

import io.github.wkktoria.shareall.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserViewModel {
    private long id;
    private String username;
    private String displayName;
    private String image;

    public UserViewModel(final User user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setDisplayName(user.getDisplayName());
        this.setImage(user.getImage());
    }
}
