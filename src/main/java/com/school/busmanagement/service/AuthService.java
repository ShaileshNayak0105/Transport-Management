package com.school.busmanagement.service;

import com.school.busmanagement.dto.CreateParentRequest;
import com.school.busmanagement.entity.Role;
import com.school.busmanagement.entity.Student;
import com.school.busmanagement.entity.User;
import com.school.busmanagement.exception.DuplicateEmailException;
import com.school.busmanagement.exception.ResourceNotFoundException;
import com.school.busmanagement.repository.StudentRepository;
import com.school.busmanagement.repository.UserRepository;
import com.school.busmanagement.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public void createDefaultAdminIfNotExists() {
        if (userRepository.existsByEmail("admin@school.com")) {
            return;
        }

        User admin = User.builder()
                .name("System Administrator")
                .email("admin@school.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
    }

    public User createParent(CreateParentRequest request) {
        validateParentEmail(request.getEmail());

        if (request.getStudentId() == null) {
            throw new ResourceNotFoundException("Student not found with id: null");
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("A user with this email already exists.");
        }

        User parent = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.PARENT)
                .student(student)
                .build();

        return userRepository.save(parent);
    }

    public String getDashboardRedirect(CustomUserDetails userDetails) {
        if (userDetails.getUser().getRole() == Role.ADMIN) {
            return "/admin/dashboard";
        }
        return "/parent/dashboard";
    }

    private void validateParentEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Parent email must contain '@'.");
        }
    }
}
