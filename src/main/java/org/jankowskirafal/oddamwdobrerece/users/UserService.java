package org.jankowskirafal.oddamwdobrerece.users;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findUserByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//        return UserPrincipal.build(user);
//    }


    public Page<User> getAllUsersByRoleName(int page, int size, String roleName) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("email").ascending());
        return userRepository.findByRoleName(roleName, pageable);
    }

    public User createUser(User user, List<String> roleNames) {
        Set<Authority> authorities = roleNames.stream()
                .map(roleName -> authorityRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Authority not found for role: " + roleName)))
                .collect(Collectors.toSet());

        user.setAuthorities(authorities);
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void registerUser(RegistrationDto registrationDto) {
        User user = new User();
        user.setEmail(registrationDto.email());
        user.setPassword(passwordEncoder.encode(registrationDto.password()));

        user.setActive(true);

        Authority userRole = authorityRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_USER not found."));
        user.setAuthorities(Set.of(userRole));

        userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }


}
