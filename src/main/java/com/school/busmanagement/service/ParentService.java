package com.school.busmanagement.service;

import com.school.busmanagement.dto.ParentDashboardResponse;
import com.school.busmanagement.entity.Role;
import com.school.busmanagement.entity.Student;
import com.school.busmanagement.entity.User;
import com.school.busmanagement.exception.ResourceNotFoundException;
import com.school.busmanagement.repository.StudentRepository;
import com.school.busmanagement.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public List<User> getAllParents() {
        return userRepository.findByRole(Role.PARENT);
    }

    public long countParents() {
        return userRepository.countByRole(Role.PARENT);
    }

    public List<Student> getStudentsWithoutParents() {
        List<Long> linkedStudentIds = userRepository.findByRole(Role.PARENT).stream()
                .map(User::getStudent)
                .filter(student -> student != null)
                .map(Student::getId)
                .toList();

        return studentRepository.findAll().stream()
                .filter(student -> !linkedStudentIds.contains(student.getId()))
                .toList();
    }

    public ParentDashboardResponse getDashboardForParent(String email) {
        User parent = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Parent account was not found."));

        if (parent.getStudent() == null) {
            throw new ResourceNotFoundException("No student linked to this account");
        }

        Student student = parent.getStudent();

        ParentDashboardResponse response = new ParentDashboardResponse();
        response.setStudentName(student.getName());
        response.setClassName(student.getClassName());
        response.setBusNumber(student.getBus() != null ? student.getBus().getBusNumber() : "Not Assigned");
        response.setBusCapacity(student.getBus() != null ? student.getBus().getCapacity() : 0);
        response.setRouteName(student.getRoute() != null ? student.getRoute().getRouteName() : "Not Assigned");
        response.setPickupPoints(student.getRoute() != null ? student.getRoute().getPickupPoints() : "Not Available");
        response.setDriverName(student.getBus() != null && student.getBus().getDriver() != null
                ? student.getBus().getDriver().getName()
                : "Not Assigned");
        response.setDriverPhone(student.getBus() != null && student.getBus().getDriver() != null
                ? student.getBus().getDriver().getPhone()
                : "Not Available");
        return response;
    }

    public ParentDashboardResponse getDashboardData(String email) {
        return getDashboardForParent(email);
    }
}
