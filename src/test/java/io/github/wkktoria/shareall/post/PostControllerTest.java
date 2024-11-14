package io.github.wkktoria.shareall.post;

import io.github.wkktoria.shareall.error.ApiError;
import io.github.wkktoria.shareall.user.UserRepository;
import io.github.wkktoria.shareall.user.UserService;
import org.junit.jupiter.api.BeforeEach;
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

import static io.github.wkktoria.shareall.TestUtil.createValidPost;
import static io.github.wkktoria.shareall.TestUtil.createValidUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PostControllerTest {
    private static final String API_1_0_POSTS = "/api/1.0/posts";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
        postRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    void createPost_whenPostIsValidAndUserIsAuthorized_receiveOk() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = createValidPost();

        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void createPost_whenPostIsValidAndUserIsUnauthorized_receiveUnauthorized() {
        Post post = createValidPost();

        ResponseEntity<Object> response = postPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createPost_whenPostIsValidAndUserIsUnauthorized_receiveApiError() {
        Post post = createValidPost();

        ResponseEntity<ApiError> response = postPost(post, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void createPost_whenPostIsValidAndUserIsAuthorized_postSavedToDatabase() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = createValidPost();
        postPost(post, Object.class);

        assertThat(postRepository.count()).isEqualTo(1);
    }

    @Test
    void createPost_whenPostIsValidAndUserIsAuthorized_postSavedToDatabaseWithTimestamp() {
        userService.save(createValidUser("user1"));
        authenticate("user1");

        Post post = createValidPost();
        postPost(post, Object.class);

        Post inDbpost = postRepository.findAll().getFirst();

        assertThat(inDbpost.getTimestamp()).isNotNull();
    }

    private <T> ResponseEntity<T> postPost(final Post post, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_POSTS, post, responseType);
    }

    private void authenticate(final String username) {
        testRestTemplate
                .getRestTemplate()
                .getInterceptors()
                .add(new BasicAuthenticationInterceptor(username, "P4sW@ord"));
    }
}
