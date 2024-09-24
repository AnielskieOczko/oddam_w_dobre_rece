package org.jankowskirafal.oddamwdobrerece.users;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByEmail(String email);

    void saveUser(User user);
}
