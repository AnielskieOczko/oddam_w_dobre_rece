package org.jankowskirafal.oddamwdobrerece.users;

import java.util.List;
import java.util.Optional;

public interface AuthorityService {

    void addNewAuthority(Authority authority);

    List<Authority> getAllAuthorities();

    Optional<Authority> findAuthorityByRoleName(String roleName);
}
