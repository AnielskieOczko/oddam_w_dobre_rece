package org.jankowskirafal.oddamwdobrerece.users;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public void addNewAuthority(Authority authority) {
        authorityRepository.save(authority);
    }
}
