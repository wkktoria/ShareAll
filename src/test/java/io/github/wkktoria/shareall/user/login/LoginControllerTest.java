package io.github.wkktoria.shareall.user.login;

import io.github.wkktoria.shareall.error.ApiError;
import io.github.wkktoria.shareall.user.User;
import io.github.wkktoria.shareall.user.UserRepository;
import io.github.wkktoria.shareall.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.Objects;

import static io.github.wkktoria.shareall.TestUtil.createValidUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LoginControllerTest {
    private static final String API_1_0_LOGIN = "/api/1.0/login";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

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

    @Test
    void postLogin_withValidCredentials_receiveOk() {
        User user = createValidUser();
        userService.save(user);

        authenticate();
        ResponseEntity<Object> response = login(Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postLogin_withValidCredentials_receiveLoggedInUserId() {
        User inDbUser = userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();
        assert body != null;
        Long id = Long.valueOf(body.get("id").toString());

        assertThat(id).isEqualTo(inDbUser.getId());
    }

    @Test
    void postLogin_withValidCredentials_receiveLoggedInUserImage() {
        User inDbUser = userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();
        assert body != null;
        String image = body.get("image").toString();

        assertThat(image).isEqualTo(inDbUser.getImage());
    }

    @Test
    void postLogin_withValidCredentials_receiveLoggedInUserDisplayName() {
        User inDbUser = userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();
        assert body != null;
        String displayName = body.get("displayName").toString();

        assertThat(displayName).isEqualTo(inDbUser.getDisplayName());
    }

    @Test
    void postLogin_withValidCredentials_receiveLoggedInUserUsername() {
        User inDbUser = userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();
        assert body != null;
        String username = body.get("username").toString();

        assertThat(username).isEqualTo(inDbUser.getUsername());
    }

    @Test
    void postLogin_withValidCredentials_notReceiveLoggedInUserPassword() {
        userService.save(createValidUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<>() {
        });

        Map<String, Object> body = response.getBody();

        assert body != null;
        assertThat(body.containsKey("password")).isFalse();
    }

    public <T> ResponseEntity<T> login(Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_LOGIN, null, responseType);
    }

    public <T> ResponseEntity<T> login(ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(API_1_0_LOGIN, HttpMethod.POST, null, responseType);
    }

    private void authenticate() {
        testRestTemplate.getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor("test-user", "P4sW@ord"));
    }
}
