package com.example.demo.services;

import com.example.demo.dtos.CompanyDto;
import com.example.demo.entities.Company;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CompanyService {
    Iterable<CompanyDto> getAllCompanies();
    Optional<Company> getCompany(Long id);
    Company saveCompany(Company company);
    void deleteCompany(Long id);
}
