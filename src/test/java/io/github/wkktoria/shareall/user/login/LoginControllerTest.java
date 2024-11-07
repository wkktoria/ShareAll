package io.github.wkktoria.shareall.user.login;

import io.github.wkktoria.shareall.error.ApiError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginControllerTest {
    private static final String API_1_0_LOGIN = "/api/1.0/login";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void postLogin_withoutUserCredentials_receiveUnauthorized() {
        ResponseEntity<Object> response = login(Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void postLogin_withIncorrectCredentials_receiveUnauthorized() {
        authenticate();
        ResponseEntity<Object> response = login(Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void postLogin_withoutUserCredentials_receiveApiError() {
        ResponseEntity<ApiError> response = login(ApiError.class);

        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).isEqualTo(API_1_0_LOGIN);
    }


    @Test
    void postLogin_withoutUserCredentials_receiveApiErrorWithoutValidationErrors() {
        ResponseEntity<String> response = login(String.class);

        assertThat(Objects.requireNonNull(response.getBody()).contains("validationErrors")).isFalse();
    }

    @Test
    void postLogin_withIncorrectCredentials_receiveUnauthorizedWithoutWWWAuthenticateHeader() {
        authenticate();
        ResponseEntity<Object> response = login(Object.class);

        assertThat(response.getHeaders().containsKey("WWW-Authenticate")).isFalse();
    }

    public <T> ResponseEntity<T> login(Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_LOGIN, null, responseType);
    }

    private void authenticate() {
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor("test-user", "P4sW@ord"));
    }
}
