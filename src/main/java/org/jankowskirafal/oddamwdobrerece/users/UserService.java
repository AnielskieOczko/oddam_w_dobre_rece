package org.jankowskirafal.oddamwdobrerece.users;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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

    public User addUser(User user) {
        return userRepository.save(user);
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


}
