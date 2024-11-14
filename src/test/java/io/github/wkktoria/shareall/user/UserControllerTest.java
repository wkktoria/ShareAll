package io.github.wkktoria.shareall.user;

import io.github.wkktoria.shareall.TestPage;
import io.github.wkktoria.shareall.TestUtil;
import io.github.wkktoria.shareall.config.AppConfig;
import io.github.wkktoria.shareall.error.ApiError;
import io.github.wkktoria.shareall.shared.GenericResponse;
import io.github.wkktoria.shareall.user.viewmodel.UserUpdateViewModel;
import io.github.wkktoria.shareall.user.viewmodel.UserViewModel;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.wkktoria.shareall.TestUtil.createValidUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {
    private static final String API_1_0_USERS = "/api/1.0/users";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AppConfig appConfig;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @AfterEach
    void cleanDirectory() throws IOException {
        FileUtils.cleanDirectory(new File(appConfig.getFullProfileImagesPath()));
        FileUtils.cleanDirectory(new File(appConfig.getFullAttachmentsPath()));
    }

    @Test
    void postUser_whenUserIsValid_receiveOk() {
        User user = createValidUser();

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

    }

    @Test
    void postUser_whenUserIsValid_userSavedToDatabase() {
        User user = createValidUser();

        postSignup(user, Object.class);

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void postUser_whenUserIsValid_receiveSuccessMessage() {
        User user = createValidUser();

        ResponseEntity<GenericResponse> response = postSignup(user, GenericResponse.class);

        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isNotNull();
    }

    @Test
    void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
        User user = createValidUser();

        postSignup(user, Object.class);
        List<User> users = userRepository.findAll();
        User inDbUser = users.getFirst();

        assertThat(inDbUser.getPassword()).isNotEqualTo(user.getPassword());
    }

    @Test
    void postUser_whenUserHasNullUsername_receiveBadRequest() {
        User user = createValidUser();
        user.setUsername(null);

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasNullDisplayName_receiveBadRequest() {
        User user = createValidUser();
        user.setDisplayName(null);

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasNullPassword_receiveBadRequest() {
        User user = createValidUser();
        user.setPassword(null);

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasUsernameWithLessThanRequired_receiveBadRequest() {
        User user = createValidUser();
        user.setUsername("abc");

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasDisplayNameWithLessThanRequired_receiveBadRequest() {
        User user = createValidUser();
        user.setDisplayName("abc");

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordWithLessThanRequired_receiveBadRequest() {
        User user = createValidUser();
        user.setPassword("P4sswd");

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasUsernameExceedsLengthLimit_receiveBadRequest() {
        User user = createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256)
                .mapToObj(n -> "a").collect(Collectors.joining());
        user.setUsername(valueOf256Chars);

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasDisplayNameExceedsLengthLimit_receiveBadRequest() {
        User user = createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256)
                .mapToObj(n -> "a").collect(Collectors.joining());
        user.setDisplayName(valueOf256Chars);

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordExceedsLengthLimit_receiveBadRequest() {
        User user = createValidUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256)
                .mapToObj(n -> "a").collect(Collectors.joining());
        user.setPassword(valueOf256Chars + "A1");

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordWithAllLowercase_receiveBadRequest() {
        User user = createValidUser();
        user.setPassword("alllowercase");

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordWithAllUppercase_receiveBadRequest() {
        User user = createValidUser();
        user.setPassword("ALLUPPERCASE");

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordWithAllNumber_receiveBadRequest() {
        User user = createValidUser();
        user.setPassword("123456789");

        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserIsInvalid_receiveApiError() {
        User user = new User();
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);

        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).isEqualTo(API_1_0_USERS);
    }

    @Test
    void postUser_whenUserIsInvalid_receiveApiErrorWithValidationErrors() {
        User user = new User();
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);

        assertThat(Objects.requireNonNull(response.getBody()).getValidationErrors().size()).isEqualTo(3);
    }

    @Test
    void postUser_whenUserHasNullUsername_receiveMessageOfNullErrorForUsername() {
        User user = createValidUser();
        user.setUsername(null);

        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = Objects.requireNonNull(response.getBody()).getValidationErrors();

        assertThat(validationErrors.get("username")).isEqualTo("Username cannot be null");
    }

    @Test
    void postUser_whenUserHasNullPassword_receiveGenericMessageOfNullError() {
        User user = createValidUser();
        user.setPassword(null);

        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = Objects.requireNonNull(response.getBody()).getValidationErrors();

        assertThat(validationErrors.get("password")).isEqualTo("Cannot be null");
    }

    @Test
    void postUser_whenUserHasInvalidLengthUsername_receiveGenericMessageOfSizeError() {
        User user = createValidUser();
        user.setUsername("abc");

        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = Objects.requireNonNull(response.getBody()).getValidationErrors();

        assertThat(validationErrors.get("username")).isEqualTo("It must have minimum 4 and maximum 255 characters");
    }

    @Test
    void postUser_whenUserHasInvalidPasswordPattern_receiveMessageOfPasswordPatternError() {
        User user = createValidUser();
        user.setPassword("alllowercase");

        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = Objects.requireNonNull(response.getBody()).getValidationErrors();

        assertThat(validationErrors.get("password"))
                .isEqualTo("Password must have at least one uppercase, one lowercase, one number and one special character");
    }

    @Test
    void postUser_whenAnotherUserHasSameUsername_receiveBadRequest() {
        userRepository.save(createValidUser());

        User user = createValidUser();
        ResponseEntity<Object> response = postSignup(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenAnotherUserHasSameUsername_receiveMessageOfDuplicateUsername() {
        userRepository.save(createValidUser());

        User user = createValidUser();
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = Objects.requireNonNull(response.getBody()).getValidationErrors();

        assertThat(validationErrors.get("username")).isEqualTo("This name is in use");
    }

    @Test
    void getUsers_whenThereAreNoUsersInDb_receiveOk() {
        ResponseEntity<Object> response = getUsers(new ParameterizedTypeReference<>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUsers_whenThereAreNoUsersInDb_receivePageWithZeroItems() {
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements()).isEqualTo(0);
    }

    @Test
    void getUsers_whenThereIsUserInDb_receivePageWithUser() {
        userRepository.save(createValidUser());
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getNumberOfElements()).isEqualTo(1);
    }

    @Test
    void getUsers_whenThereIsUserInDb_receiveUserWithoutPassword() {
        userRepository.save(createValidUser());
        ResponseEntity<TestPage<Map<String, Object>>> response = getUsers(new ParameterizedTypeReference<>() {
        });
        Map<String, Object> entity = Objects.requireNonNull(response.getBody()).getContent().getFirst();
        assertThat(entity.containsKey("password")).isFalse();
    }

    @Test
    void getUsers_whenPageIsRequestedForThreeItemsPerPageWhereDatabaseHasTwentyUsers_receiveThreeUsers() {
        IntStream.rangeClosed(1, 20).mapToObj(i -> "test-user-" + i)
                .map(TestUtil::createValidUser)
                .forEach(userRepository::save);
        String path = API_1_0_USERS + "?page=0&size=3";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getContent().size()).isEqualTo(3);
    }

    @Test
    void getUsers_whenPageSizeNotProvided_receivePageSizeAsTen() {
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(10);
    }

    @Test
    void getUsers_whenPageSizeIsGreaterThanHundred_receivePageSizeAsHundred() {
        final String path = API_1_0_USERS + "?size=500";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(100);
    }

    @Test
    void getUsers_whenPageSizeIsNegative_receivePageSizeAsTen() {
        final String path = API_1_0_USERS + "?size=-1";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getSize()).isEqualTo(10);
    }

    @Test
    void getUsers_whenPageIsNegative_receiveFirstPage() {
        final String path = API_1_0_USERS + "?page=-1";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getNumber()).isEqualTo(0);
    }

    @Test
    void getUsers_whenUserLoggedIn_receivePageWithoutLoggedInUser() {
        userService.save(createValidUser("user1"));
        userService.save(createValidUser("user2"));
        userService.save(createValidUser("user3"));
        authenticate("user1");
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<>() {
        });
        assertThat(Objects.requireNonNull(response.getBody()).getTotalElements()).isEqualTo(2);
    }

    @Test
    void getUserByUsername_whenUserExists_receiveOk() {
        final String username = "test-user";
        userService.save(TestUtil.createValidUser(username));
        ResponseEntity<Object> response = getUser(username, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUserByUsername_whenUserExists_receiveUserWithoutPassword() {
        final String username = "test-user";
        userService.save(TestUtil.createValidUser(username));
        ResponseEntity<String> response = getUser(username, String.class);
        assertThat(Objects.requireNonNull(response.getBody()).contains("password")).isFalse();
    }

    @Test
    void getUserByUsername_whenUserDoesNotExist_receiveNotFound() {
        ResponseEntity<Object> response = getUser("unknown-user", Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getUserByUsername_whenUserDoesNotExist_receiveApiError() {
        ResponseEntity<ApiError> response = getUser("unknown-user", ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage().contains("unknown-user")).isTrue();
    }

    @Test
    void putUser_whenUnauthorizedUserSendsRequest_receiveUnauthorized() {
        ResponseEntity<Object> response = putUser(123, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveForbidden() {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        final long anotherUserId = user.getId() + 123;
        ResponseEntity<Object> response = putUser(anotherUserId, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void putUser_whenUnauthorizedUserSendsRequest_receiveApiError() {
        ResponseEntity<ApiError> response = putUser(123, null, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).contains("users/123");
    }

    @Test
    void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveApiError() {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        final long anotherUserId = user.getId() + 123;
        ResponseEntity<ApiError> response = putUser(anotherUserId, null, ApiError.class);
        assertThat(Objects.requireNonNull(response.getBody()).getUrl()).contains("users/" + anotherUserId);
    }

    @Test
    void putUser_whenValidRequestBodyFromAuthorizedUser_receiveOk() {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateViewModel updatedUser = createValidUserUpdateViewModel();

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<Object> response = putUser(user.getId(), requestEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void putUser_whenValidRequestBodyFromAuthorizedUser_displayNameupdated() {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateViewModel updatedUser = createValidUserUpdateViewModel();

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        putUser(user.getId(), requestEntity, Object.class);

        User userInDb = userRepository.findByUsername("user1");
        assertThat(userInDb.getDisplayName()).isEqualTo(updatedUser.getDisplayName());
    }

    @Test
    void putUser_whenValidRequestBodyFromAuthorizedUser_receiveUserViewModelWithUpdatedDisplayName() {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateViewModel updatedUser = createValidUserUpdateViewModel();

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserViewModel> response = putUser(user.getId(), requestEntity, UserViewModel.class);
        assertThat(Objects.requireNonNull(response.getBody()).getDisplayName()).isEqualTo(updatedUser.getDisplayName());
    }

    @Test
    void putUser_withValidRequestBodyWithSupportedImageFromAuthorizedUser_receiveUserViewModelWithRandomImageName() throws IOException {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        final String imageString = readFileToBase64("profile.png");

        UserUpdateViewModel updatedUser = createValidUserUpdateViewModel();
        updatedUser.setImage(imageString);

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserViewModel> response = putUser(user.getId(), requestEntity, UserViewModel.class);
        assertThat(Objects.requireNonNull(response.getBody()).getImage()).isNotEqualTo("profile-image.png");
    }

    @Test
    void putUser_withValidRequestBodyWithSupportedImageFromAuthorizedUser_imageIsStoredUnderProfileFolder() throws IOException {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        final String imageString = readFileToBase64("profile.png");

        UserUpdateViewModel updatedUser = createValidUserUpdateViewModel();
        updatedUser.setImage(imageString);

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserViewModel> response = putUser(user.getId(), requestEntity, UserViewModel.class);

        final String storedImageName = Objects.requireNonNull(response.getBody()).getImage();
        final String profilePicturePath = appConfig.getFullProfileImagesPath() + "/" + storedImageName;

        File storedImage = new File(profilePicturePath);
        assertThat(storedImage.exists()).isTrue();
    }

    @Test
    void putUser_withInvalidRequestBodyWithNullDisplayNameFromAuthorizedUser_receiveBadRequest() {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateViewModel updatedUser = new UserUpdateViewModel();

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<Object> response = putUser(user.getId(), requestEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void putUser_withInvalidRequestBodyWithLessThanMinSizeForDisplayNameFromAuthorizedUser_receiveBadRequest() {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateViewModel updatedUser = new UserUpdateViewModel();
        updatedUser.setDisplayName("abc");

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<Object> response = putUser(user.getId(), requestEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void putUser_withInvalidRequestBodyWithMoreThanMaxSizeForDisplayNameFromAuthorizedUser_receiveBadRequest() {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());
        UserUpdateViewModel updatedUser = new UserUpdateViewModel();
        final String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(n -> "a").collect(Collectors.joining());
        updatedUser.setDisplayName(valueOf256Chars);

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<Object> response = putUser(user.getId(), requestEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void putUser_withValidRequestBodyWithJpgImageFromAuthorizedUser_receiveOk() throws IOException {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        final String imageString = readFileToBase64("test-jpg.jpg");

        UserUpdateViewModel updatedUser = createValidUserUpdateViewModel();
        updatedUser.setImage(imageString);

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserViewModel> response = putUser(user.getId(), requestEntity, UserViewModel.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void putUser_withValidRequestBodyWithGifImageFromAuthorizedUser_receiveBadRequest() throws IOException {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        final String imageString = readFileToBase64("test-gif.gif");

        UserUpdateViewModel updatedUser = createValidUserUpdateViewModel();
        updatedUser.setImage(imageString);

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<Object> response = putUser(user.getId(), requestEntity, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void putUser_withValidRequestBodyWithTxtImageFromAuthorizedUser_receiveValidationErrorForProfileImage() throws IOException {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        final String imageString = readFileToBase64("test-txt.txt");

        UserUpdateViewModel updatedUser = createValidUserUpdateViewModel();
        updatedUser.setImage(imageString);

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<ApiError> response = putUser(user.getId(), requestEntity, ApiError.class);

        Map<String, String> validationErrors = Objects.requireNonNull(response.getBody()).getValidationErrors();
        assertThat(validationErrors.get("image")).isEqualTo("Only PNG and JPG files are allowed");
    }

    @Test
    void putUser_withValidRequestBodyWithJpgImageForUserWhoHasImage_removesOldImageFromStorage() throws IOException {
        User user = userService.save(createValidUser("user1"));
        authenticate(user.getUsername());

        final String imageString = readFileToBase64("test-jpg.jpg");

        UserUpdateViewModel updatedUser = createValidUserUpdateViewModel();
        updatedUser.setImage(imageString);

        HttpEntity<UserUpdateViewModel> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserViewModel> response = putUser(user.getId(), requestEntity, UserViewModel.class);
        putUser(user.getId(), requestEntity, UserViewModel.class);

        String storedImageName = Objects.requireNonNull(response.getBody()).getImage();
        String profilePicturePath = appConfig.getFullProfileImagesPath() + "/" + storedImageName;
        File storedImage = new File(profilePicturePath);

        assertThat(storedImage.exists()).isFalse();
    }

    private void authenticate(final String username) {
        testRestTemplate
                .getRestTemplate()
                .getInterceptors()
                .add(new BasicAuthenticationInterceptor(username, "P4sW@ord"));
    }

    private UserUpdateViewModel createValidUserUpdateViewModel() {
        UserUpdateViewModel updatedUser = new UserUpdateViewModel();
        updatedUser.setDisplayName("newDisplayName");
        return updatedUser;
    }

    private String readFileToBase64(final String filename) throws IOException {
        ClassPathResource imageResource = new ClassPathResource(filename);
        byte[] imageArray = FileUtils.readFileToByteArray(imageResource.getFile());
        return Base64.getEncoder().encodeToString(imageArray);
    }

    public <T> ResponseEntity<T> postSignup(Object request, Class<T> response) {
        return testRestTemplate.postForEntity(API_1_0_USERS, request, response);
    }

    public <T> ResponseEntity<T> getUsers(ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(API_1_0_USERS, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getUsers(final String path, ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getUser(final String username, Class<T> responseType) {
        final String path = API_1_0_USERS + "/" + username;
        return testRestTemplate.getForEntity(path, responseType);
    }

    public <T> ResponseEntity<T> putUser(final long id, HttpEntity<?> requestEntity, Class<T> responseType) {
        final String path = API_1_0_USERS + "/" + id;
        return testRestTemplate.exchange(path, HttpMethod.PUT, requestEntity, responseType);
    }
}
