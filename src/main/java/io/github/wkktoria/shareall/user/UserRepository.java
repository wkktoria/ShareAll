package io.github.wkktoria.shareall.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsernameContaining(final String username);

    User findByUsernameAndDisplayName(final String username, final String displayName);

    User findByUsername(final String username);
}
