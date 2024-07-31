package org.jankowskirafal.oddamwdobrerece.users;

import jakarta.transaction.Transactional;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);


    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a.name = :roleName ORDER BY u.email ASC")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    @Modifying
    @Query("UPDATE User u " +
            "SET u.email = :email, u.password = :password, u.isActive = :isActive " +
            "WHERE u.id = :userId")
    void updateUser(@Param("userId") Long userId,
                    @Param("email") String email,
                    @Param("password") String password,
                    @Param("isActive") boolean isActive);

    @Modifying
    @Query("DELETE FROM UserAuthority ua WHERE ua.user.id = :userId")
    void deleteUserAuthorities(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("INSERT INTO UserAuthority (user, authority) " +
            "SELECT u, a FROM User u, Authority a " +
            "WHERE u.id = :userId AND a.name IN (:authorityNames)") // Correct entity name
    void insertUserAuthorities(@Param("userId") Long userId, @Param("authorityNames") Set<String> authorityNames);

}