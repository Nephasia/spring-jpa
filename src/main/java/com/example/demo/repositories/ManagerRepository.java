package com.example.demo.repositories;

import com.example.demo.entities.Manager;
import org.springframework.data.repository.CrudRepository;

public interface ManagerRepository extends CrudRepository<Manager, Long> {
}
