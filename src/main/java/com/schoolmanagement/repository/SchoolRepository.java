package com.schoolmanagement.repository;

import com.schoolmanagement.entity.School;
import com.schoolmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, String> {
    boolean existsByCode(String code);

    Optional<School> findByCode(String code);

    Optional<School> findByPrincipal(User principal);
}