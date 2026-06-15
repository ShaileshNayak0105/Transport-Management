package com.school.busmanagement.repository;

import com.school.busmanagement.entity.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Used to get all students assigned to a bus so we can validate bus capacity.
    List<Student> findByBusId(Long busId);
}
