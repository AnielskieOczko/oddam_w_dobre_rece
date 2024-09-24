package org.jankowskirafal.oddamwdobrerece.users;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    Page<User> getAllUsersByRoleName(int page, int size, String roleName);

    User createUser(User user, Set<String> roleNames);

    void deleteUserById(Long id);

    List<User> getAll();

    Optional<User> getByEmail(String email);

    void registerUser(RegistrationDto registrationDto);

    Optional<User> getUserById(Long id);

    void updateUser(User user, Set<String> roleNames);

    User updateUser(User user);

    Optional<User> findUserByEmail(String email);

    void saveUser(User user);
}
