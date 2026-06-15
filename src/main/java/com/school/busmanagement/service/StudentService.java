package com.school.busmanagement.service;

import com.school.busmanagement.dto.CreateStudentRequest;
import com.school.busmanagement.entity.Bus;
import com.school.busmanagement.entity.Route;
import com.school.busmanagement.entity.Student;
import com.school.busmanagement.exception.BusFullException;
import com.school.busmanagement.exception.ResourceNotFoundException;
import com.school.busmanagement.repository.BusRepository;
import com.school.busmanagement.repository.RouteRepository;
import com.school.busmanagement.repository.StudentRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public long countStudents() {
        return studentRepository.count();
    }

    public Map<Long, Integer> getStudentCountsByBus() {
        return studentRepository.findAll().stream()
                .filter(student -> student.getBus() != null)
                .collect(Collectors.groupingBy(student -> student.getBus().getId(), Collectors.summingInt(student -> 1)));
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Transactional
    public Student createStudent(CreateStudentRequest request) {
        validateStudentName(request.getName());

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected bus was not found."));

        long assignedStudents = studentRepository.findByBusId(bus.getId()).size();
        if (assignedStudents >= bus.getCapacity()) {
            throw new BusFullException("Bus is at full capacity");
        }

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected route was not found."));

        if (bus.getRoute() != null && !bus.getRoute().getId().equals(route.getId())) {
            throw new IllegalArgumentException("Selected route must match the route assigned to the selected bus.");
        }

        Student student = Student.builder()
                .name(request.getName())
                .age(request.getAge())
                .className(request.getClassName())
                .bus(bus)
                .route(route)
                .build();

        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, CreateStudentRequest request) {
        validateStudentName(request.getName());

        Student student = getStudentById(id);
        Long currentBusId = student.getBus() != null ? student.getBus().getId() : null;

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected bus was not found."));

        if (!bus.getId().equals(currentBusId)) {
            long assignedStudents = studentRepository.findByBusId(bus.getId()).size();
            if (assignedStudents >= bus.getCapacity()) {
                throw new BusFullException("Bus is at full capacity");
            }
        }

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected route was not found."));

        if (bus.getRoute() != null && !bus.getRoute().getId().equals(route.getId())) {
            throw new IllegalArgumentException("Selected route must match the route assigned to the selected bus.");
        }

        student.setName(request.getName());
        student.setAge(request.getAge());
        student.setClassName(request.getClassName());
        student.setBus(bus);
        student.setRoute(route);
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    private void validateStudentName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
    }
}
