package com.example.demo.repositories;

import com.example.demo.entities.Company;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompanyRepository extends CrudRepository<Company, Long> {
    @EntityGraph(attributePaths = {"departments"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Company> findById(Long id);
}
