package io.github.wkktoria.shareall.error;

import lombok.*;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ApiError {
    private long timestamp = new Date().getTime();

    private int status;

    private String message;

    private String url;

    private Map<String, String> validationErrors;

    public ApiError(final int status, final String message, final String url) {
        super();
        this.status = status;
        this.message = message;
        this.url = url;
    }
}