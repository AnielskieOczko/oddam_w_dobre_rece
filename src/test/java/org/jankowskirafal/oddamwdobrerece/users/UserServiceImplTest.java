package org.jankowskirafal.oddamwdobrerece.users;

import org.jankowskirafal.oddamwdobrerece.donations.Donation;
import org.jankowskirafal.oddamwdobrerece.donations.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Authority roleUser, roleAdmin;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");

        roleUser = new Authority();
        roleUser.setName("ROLE_USER");

        roleAdmin = new Authority();
        roleAdmin.setName("ROLE_ADMIN");
    }

    @Test
    void getAllUsersByRoleName_shouldReturnPageOfUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("email").ascending());
        Page<User> expectedPage = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepository.findByRoleName("ROLE_USER", pageable)).thenReturn(expectedPage);

        // Act
        Page<User> result = userService.getAllUsersByRoleName(0, 10, "ROLE_USER");

        // Assert
        assertThat(result).isEqualTo(expectedPage);
    }

    @Test
    void createUser_shouldCreateUserWithEncodedPasswordAndAuthorities() {
        // Arrange
        when(passwordEncoder.encode("password")).thenReturn("{bcrypt}encodedPassword");
        when(authorityRepository.findByNameIn(Set.of("ROLE_USER"))).thenReturn(Set.of(roleUser));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User createdUser = userService.createUser(user, Set.of("ROLE_USER"));

        // Assert
        assertThat(createdUser.getPassword()).isEqualTo("{bcrypt}encodedPassword");
        assertThat(createdUser.getAuthorities()).containsExactly(roleUser);
    }

    @Test
    void createUser_shouldThrowExceptionWhenAuthoritiesNotFound() {
        // Arrange
        when(authorityRepository.findByNameIn(anySet())).thenReturn(Collections.emptySet());

        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(user, Set.of("ROLE_NONEXISTENT")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("One or more authorities not found");
    }

    @Test
    void deleteUserById_shouldDeleteUserAndRemoveUserFromDonations() {
        // Arrange
        Donation donation = new Donation();
        donation.setUser(user);
        user.setDonations(Set.of(donation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUserById(1L);

        // Assert
        ArgumentCaptor<Donation> donationCaptor = ArgumentCaptor.forClass(Donation.class);
        verify(donationRepository).save(donationCaptor.capture());
        assertThat(donationCaptor.getValue().getUser()).isNull();
        verify(userRepository).delete(user);
    }

    //TODO
    // Add similar tests for other methods in UserServiceImpl:
    // - getByEmail
    // - registerUser
    // - getUserById
    // - updateUser(User user, Set<String> roleNames)
    // - updateUser(User user)

    @Test
    void getUserByEmail_shouldReturnOneUser() {

        // given
        String email = "test@example.com";

        // when
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.ofNullable(user));
        Optional<User> expectedUser = userService.getByEmail(email);

        // then
        assertThat(expectedUser.isPresent()).isTrue();
        assertThat(expectedUser.get().getEmail()).isEqualTo(email);

    }

    @Test
    void
    getUserByEmail_shouldThrowExceptionWhenUserNotFound() {

        // given & when
        String email = "notRegisteredUser@example.com";
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        Optional<User> expectedUser = userService.getByEmail(email);

        // then
        assertThat(expectedUser.isPresent()).isFalse();
    }

    @Test
    void registerUser_shouldRegisterNewUser() {

        //given & when
        Set<String> roles = Set.of("ROLE_USER");
        when(authorityRepository.findByNameIn(roles)).thenReturn(Set.of(roleUser));
        when(userRepository.save(user)).thenReturn(user);
        userService.createUser(user, roles);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo(user.getEmail());

    }

    @Test
    void getUserById_shouldReturnUser() {

        // given
        Long id = 1L;

        // when
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));
        Optional<User> expectedUser = userService.getUserById(id);

        // then
        assertThat(expectedUser.isPresent()).isTrue();
        assertThat(expectedUser.get().getId()).isEqualTo(id);
    }

    @Test
    void updateUser_shouldUpdateUserWithEncodedPasswordAndAuthorities() {
        // given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("{bcrypt}oldEncodedPassword");
        existingUser.setActive(false); // Initially inactive

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("new@example.com");
        updatedUser.setPassword("newPassword");
        updatedUser.setActive(true);

        Set<String> newRoleNames = Set.of("ROLE_ADMIN", "ROLE_USER");

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("{bcrypt}newEncodedPassword");
        when(authorityRepository.findByNameIn(newRoleNames))
                .thenReturn(Set.of(roleAdmin, roleUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser); // Return the updated user
        userService.updateUser(updatedUser, newRoleNames);

        // then
        assertThat(existingUser.getEmail()).isEqualTo("new@example.com"); // Email updated
        assertThat(existingUser.getPassword()).isEqualTo("{bcrypt}newEncodedPassword"); // Password re-encoded
        assertThat(existingUser.isActive()).isTrue(); // Active status updated
        assertThat(existingUser.getAuthorities()).containsExactlyInAnyOrder(roleAdmin, roleUser); // Roles updated

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldThrowExceptionWhenUserNotFound() {
        // given
        Long userId = 1L;
        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setEmail("old@example.com");
        userToUpdate.setPassword("{bcrypt}oldEncodedPassword");
        userToUpdate.setActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUser(userToUpdate, Set.of("ROLE_USER")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    @Test
    void updateUser_shouldThrowExceptionWhenAuthoritiesNotFound() {
        // given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("{bcrypt}oldEncodedPassword");
        existingUser.setActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(authorityRepository.findByNameIn(anySet())).thenReturn(Collections.emptySet());

        // when & then
        assertThatThrownBy(() -> userService.updateUser(existingUser, Set.of("ROLE_NONEXISTENT")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("One or more authorities not found");
    }

    @Test
    void updateUser_shouldEncodePasswordIfNotBcryptAndSave() {
        // given
        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setEmail("test@example.com");
        userToUpdate.setPassword("plainPassword"); // Password is not bcrypt encoded

        when(passwordEncoder.encode("plainPassword")).thenReturn("{bcrypt}encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        // when
        User updatedUser = userService.updateUser(userToUpdate);

        // then
        assertThat(updatedUser.getPassword()).isEqualTo("{bcrypt}encodedPassword"); // Verify password is encoded
        verify(passwordEncoder).encode("plainPassword"); // Verify encoder was called
        verify(userRepository).save(userToUpdate); // Verify repository save was called
    }

    @Test
    void updateUser_shouldNotEncodePasswordIfAlreadyBcryptAndSave() {
        // given
        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setEmail("test@example.com");
        userToUpdate.setPassword("{bcrypt}alreadyEncodedPassword"); // Password is already bcrypt encoded

        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

        // when
        User updatedUser = userService.updateUser(userToUpdate);

        // then
        assertThat(updatedUser.getPassword()).isEqualTo("{bcrypt}alreadyEncodedPassword"); // Password remains the same
        verify(passwordEncoder, never()).encode(anyString()); // Verify encoder was NOT called
        verify(userRepository).save(userToUpdate); // Verify repository save was called
    }


}