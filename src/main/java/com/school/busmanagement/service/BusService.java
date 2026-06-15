package com.school.busmanagement.service;

import com.school.busmanagement.dto.CreateBusRequest;
import com.school.busmanagement.entity.Bus;
import com.school.busmanagement.entity.Driver;
import com.school.busmanagement.entity.Route;
import com.school.busmanagement.entity.Student;
import com.school.busmanagement.exception.ResourceNotFoundException;
import com.school.busmanagement.repository.BusRepository;
import com.school.busmanagement.repository.DriverRepository;
import com.school.busmanagement.repository.RouteRepository;
import com.school.busmanagement.repository.StudentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;
    private final StudentRepository studentRepository;

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public long countBuses() {
        return busRepository.count();
    }

    public Bus getBusById(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));
    }

    @Transactional
    public Bus createBus(CreateBusRequest request) {
        validateBusCapacity(request.getCapacity());

        if (busRepository.existsByBusNumber(request.getBusNumber())) {
            throw new IllegalArgumentException("Bus number already exists.");
        }

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected driver was not found."));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected route was not found."));

        validateDriverAvailability(driver.getId(), null);

        Bus bus = Bus.builder()
                .busNumber(request.getBusNumber())
                .capacity(request.getCapacity())
                .driver(driver)
                .route(route)
                .build();

        return busRepository.save(bus);
    }

    @Transactional
    public Bus updateBus(Long id, CreateBusRequest request) {
        validateBusCapacity(request.getCapacity());

        Bus bus = getBusById(id);

        if (!bus.getBusNumber().equals(request.getBusNumber()) && busRepository.existsByBusNumber(request.getBusNumber())) {
            throw new IllegalArgumentException("Bus number already exists.");
        }

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected driver was not found."));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Selected route was not found."));

        validateDriverAvailability(driver.getId(), bus.getId());

        List<Student> assignedStudents = studentRepository.findByBusId(bus.getId());
        if (assignedStudents.size() > request.getCapacity()) {
            throw new IllegalArgumentException("Bus capacity cannot be lower than the number of assigned students.");
        }

        bus.setBusNumber(request.getBusNumber());
        bus.setCapacity(request.getCapacity());
        bus.setDriver(driver);
        bus.setRoute(route);
        return busRepository.save(bus);
    }

    @Transactional
    public void deleteBus(Long id) {
        Bus bus = getBusById(id);
        if (!studentRepository.findByBusId(id).isEmpty()) {
            throw new IllegalArgumentException("Cannot delete bus because students are assigned to it.");
        }
        busRepository.delete(bus);
    }

    private void validateDriverAvailability(Long driverId, Long currentBusId) {
        busRepository.findByDriverId(driverId)
                .filter(existingBus -> currentBusId == null || !existingBus.getId().equals(currentBusId))
                .ifPresent(existingBus -> {
                    throw new IllegalArgumentException("This driver is already assigned to another bus.");
                });
    }

    private void validateBusCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Bus capacity must be greater than 0.");
        }
    }
}
