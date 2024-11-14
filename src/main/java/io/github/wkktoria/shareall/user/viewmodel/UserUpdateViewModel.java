package io.github.wkktoria.shareall.user.viewmodel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateViewModel {
    @NotNull
    @Size(min = 4, max = 255)
    private String displayName;

    private String image;
}
