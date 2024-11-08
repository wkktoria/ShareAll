package io.github.wkktoria.shareall.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.github.wkktoria.shareall.TestUtil.createValidUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void findByUsername_whenUserExists_returnUser() {
        testEntityManager.persist(createValidUser());

        User inDbUser = userRepository.findByUsername("test-user");

        assertThat(inDbUser).isNotNull();
    }

    @Test
    void findByUsername_whenUserDoesNotExist_returnNull() {
        User inDbUser = userRepository.findByUsername("nonexistinguser");

        assertThat(inDbUser).isNull();
    }
}