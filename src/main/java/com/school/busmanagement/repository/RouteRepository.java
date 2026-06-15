package com.school.busmanagement.repository;

import com.school.busmanagement.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {

    // Helps avoid duplicate route names in the admin route management screen.
    boolean existsByRouteName(String routeName);
}
