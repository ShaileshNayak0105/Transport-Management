package com.school.busmanagement.repository;

import com.school.busmanagement.entity.Role;
import com.school.busmanagement.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // Used during login because email is the username in Spring Security.
    Optional<User> findByEmail(String email);

    // Helps prevent duplicate parent/admin accounts with the same email address.
    boolean existsByEmail(String email);

    // Used to fetch only parent users for admin screens.
    List<User> findByRole(Role role);

    // Used on the admin dashboard to show the total number of parent accounts.
    long countByRole(Role role);
}
