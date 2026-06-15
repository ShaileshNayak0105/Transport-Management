package com.school.busmanagement.service;

import com.school.busmanagement.dto.CreateRouteRequest;
import com.school.busmanagement.entity.Route;
import com.school.busmanagement.exception.ResourceNotFoundException;
import com.school.busmanagement.repository.RouteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public long countRoutes() {
        return routeRepository.count();
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
    }

    @Transactional
    public Route createRoute(CreateRouteRequest request) {
        if (routeRepository.existsByRouteName(request.getRouteName())) {
            throw new IllegalArgumentException("Route name already exists.");
        }

        Route route = Route.builder()
                .routeName(request.getRouteName())
                .pickupPoints(request.getPickupPoints())
                .build();

        return routeRepository.save(route);
    }

    @Transactional
    public Route updateRoute(Long id, CreateRouteRequest request) {
        Route route = getRouteById(id);

        if (!route.getRouteName().equals(request.getRouteName()) && routeRepository.existsByRouteName(request.getRouteName())) {
            throw new IllegalArgumentException("Route name already exists.");
        }

        route.setRouteName(request.getRouteName());
        route.setPickupPoints(request.getPickupPoints());
        return routeRepository.save(route);
    }

    @Transactional
    public void deleteRoute(Long id) {
        Route route = getRouteById(id);
        routeRepository.delete(route);
    }
}
