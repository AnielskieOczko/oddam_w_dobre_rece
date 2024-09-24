package org.jankowskirafal.oddamwdobrerece.users;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jankowskirafal.oddamwdobrerece.donations.DonationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final DonationRepository donationRepository;

    public Page<User> getAllUsersByRoleName(int page, int size, String roleName) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("email").ascending());
        return userRepository.findByRoleName(roleName, pageable);
    }

    public User createUser(User user, Set<String> roleNames) {
        Set<Authority> authorities = authorityRepository.findByNameIn(roleNames);
        if (authorities.size() != roleNames.size()) {
            throw new IllegalArgumentException("One or more authorities not found");
        }
        user.setAuthorities(authorities);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.findById(id).ifPresentOrElse(
                user -> {
                    user.getDonations().forEach(donation -> {
                        donation.setUser(null);
                        donationRepository.save(donation);
                    });
                    userRepository.delete(user);
                },
                () -> {
                    throw new IllegalArgumentException("User not found with ID: " + id);
                }
        );
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
    public Optional<User> getByEmail(String email) {
        return userRepository.findUserByEmail(email);
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

    public void updateUser(User user, Set<String> roleNames) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        existingUser.setEmail(user.getEmail());
        if (!user.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setActive(user.isActive());

        Set<Authority> newAuthorities = authorityRepository.findByNameIn(roleNames);
        if (newAuthorities.size() != roleNames.size()) {
            throw new IllegalArgumentException("One or more authorities not found");
        }
        existingUser.setAuthorities(newAuthorities);

        userRepository.save(existingUser);
    }

    public User updateUser(User user) {
        if (!user.getPassword().startsWith("{bcrypt}")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
