package com.school.busmanagement.config;

import com.school.busmanagement.entity.Bus;
import com.school.busmanagement.entity.Driver;
import com.school.busmanagement.entity.Role;
import com.school.busmanagement.entity.Route;
import com.school.busmanagement.entity.Student;
import com.school.busmanagement.entity.User;
import com.school.busmanagement.repository.BusRepository;
import com.school.busmanagement.repository.DriverRepository;
import com.school.busmanagement.repository.RouteRepository;
import com.school.busmanagement.repository.StudentRepository;
import com.school.busmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final BusRepository busRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Remove seed data before production deployment.
        createDefaultAdminIfNeeded();
        seedSampleDataIfNeeded();
    }

    private void createDefaultAdminIfNeeded() {
        if (userRepository.findByEmail("admin@school.com").isPresent()) {
            return;
        }

        User admin = User.builder()
                .name("School Admin")
                .email("admin@school.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .student(null)
                .build();

        userRepository.save(admin);
        log.info("Default admin account created: admin@school.com / admin123");
    }

    private void seedSampleDataIfNeeded() {
        Route routeA = findRouteByName("Route A");
        if (routeA == null) {
            routeA = routeRepository.save(Route.builder()
                    .routeName("Route A")
                    .pickupPoints("MG Road, Whitefield, KR Puram")
                    .build());
        }

        Route routeB = findRouteByName("Route B");
        if (routeB == null) {
            routeB = routeRepository.save(Route.builder()
                    .routeName("Route B")
                    .pickupPoints("Indiranagar, Marathahalli, Electronic City")
                    .build());
        }

        Driver driverOne = findDriverByPhone("9876543210");
        if (driverOne == null) {
            driverOne = driverRepository.save(Driver.builder()
                    .name("Ramesh Kumar")
                    .phone("9876543210")
                    .build());
        }

        Driver driverTwo = findDriverByPhone("9876543211");
        if (driverTwo == null) {
            driverTwo = driverRepository.save(Driver.builder()
                    .name("Suresh Naik")
                    .phone("9876543211")
                    .build());
        }

        Bus busOne = findBusByNumber("KA01AB1234");
        if (busOne == null) {
            busOne = busRepository.save(Bus.builder()
                    .busNumber("KA01AB1234")
                    .capacity(40)
                    .driver(driverOne)
                    .route(routeA)
                    .build());
        }

        Bus busTwo = findBusByNumber("KA02CD5678");
        if (busTwo == null) {
            busTwo = busRepository.save(Bus.builder()
                    .busNumber("KA02CD5678")
                    .capacity(35)
                    .driver(driverTwo)
                    .route(routeB)
                    .build());
        }

        Student studentOne = findStudentByName("Ananya Sharma");
        if (studentOne == null) {
            studentOne = studentRepository.save(Student.builder()
                    .name("Ananya Sharma")
                    .age(10)
                    .className("Grade 5")
                    .bus(busOne)
                    .route(routeA)
                    .build());
        }

        Student studentTwo = findStudentByName("Rahul Verma");
        if (studentTwo == null) {
            studentTwo = studentRepository.save(Student.builder()
                    .name("Rahul Verma")
                    .age(11)
                    .className("Grade 6")
                    .bus(busOne)
                    .route(routeA)
                    .build());
        }

        Student studentThree = findStudentByName("Priya Nair");
        if (studentThree == null) {
            studentThree = studentRepository.save(Student.builder()
                    .name("Priya Nair")
                    .age(9)
                    .className("Grade 4")
                    .bus(busTwo)
                    .route(routeB)
                    .build());
        }

        if (userRepository.findByEmail("parent1@school.com").isEmpty()) {
            userRepository.save(User.builder()
                    .name("Meera Sharma")
                    .email("parent1@school.com")
                    .password(passwordEncoder.encode("parent123"))
                    .role(Role.PARENT)
                    .student(studentOne)
                    .build());
        }
    }

    private Route findRouteByName(String routeName) {
        return routeRepository.findAll().stream()
                .filter(route -> route.getRouteName().equalsIgnoreCase(routeName))
                .findFirst()
                .orElse(null);
    }

    private Driver findDriverByPhone(String phone) {
        return driverRepository.findAll().stream()
                .filter(driver -> driver.getPhone().equals(phone))
                .findFirst()
                .orElse(null);
    }

    private Bus findBusByNumber(String busNumber) {
        return busRepository.findAll().stream()
                .filter(bus -> bus.getBusNumber().equalsIgnoreCase(busNumber))
                .findFirst()
                .orElse(null);
    }

    private Student findStudentByName(String name) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
