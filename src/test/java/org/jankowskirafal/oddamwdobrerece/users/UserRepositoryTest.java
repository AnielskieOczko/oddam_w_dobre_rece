package org.jankowskirafal.oddamwdobrerece.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    void findUserByEmail_shouldReturnUserWhenEmailExists() {
        // Arrange (Setup)
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        userRepository.save(user);

        // Act (Method under test)
        Optional<User> foundUser = userRepository.findUserByEmail("test@example.com");

        // Assert (Verification)
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findUserByEmail_shouldReturnEmptyOptionalWhenEmailDoesNotExist() {
        // Act
        Optional<User> foundUser = userRepository.findUserByEmail("nonexistent@example.com");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByRoleName_shouldReturnUsersWithSpecificRole() {

        // Given
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("password");

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword("password");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        Authority authority1 = new Authority();
        authority1.setName("ROLE_USER");
        authority1.setUsers(Set.of(user1));

        Authority authority2 = new Authority();
        authority2.setName("ROLE_ADMIN");
        authority2.setUsers(Set.of(user2));

        authorityRepository.save(authority1);
        authorityRepository.save(authority2);

        user1.setAuthorities(Set.of(authority1));
        user2.setAuthorities(Set.of(authority2));

        Pageable pageable = PageRequest.of(0, 10);

        // Where
        Page<User> adminUsers = userRepository.findByRoleName("ROLE_ADMIN", pageable);
        System.out.println(adminUsers);

        // Then
        assertThat(adminUsers).hasSize(1);
        assertThat(adminUsers.getContent().get(0).getEmail()).isEqualTo("user2@example.com");
    }
}