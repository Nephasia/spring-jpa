package com.example.demo.services;

import com.example.demo.dtos.CompanyDto;
import com.example.demo.entities.*;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.repositories.DepartmentRepository;
import com.example.demo.repositories.TeamRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository,
                              DepartmentRepository departmentRepository,
                              TeamRepository teamRepository) {
        this.companyRepository = companyRepository;
        this.departmentRepository = departmentRepository;
        this.teamRepository = teamRepository;
    }

    public List<CompanyDto> getAllCompanies() {
        Iterable<Company> companies = companyRepository.findAll();
        List<CompanyDto> companyDtos = StreamSupport.stream(companies.spliterator(), false)
                .map(company -> CompanyDto.fromEntity(company)).toList();
        return companyDtos;
    }

    public Optional<Company> getCompany(Long id) {
        Optional<Company> optionalCompany = companyRepository.findById(id);
        if(optionalCompany.isPresent()) {
            optionalCompany.get().getDepartments().forEach(departmentEntity -> {
                Hibernate.initialize(departmentEntity);
                departmentEntity.getTeams().forEach(teamEntity -> {
                    Hibernate.initialize(teamEntity);
                });
            });
        }

        return optionalCompany;
    }

    @Transactional
    public Company saveCompany(Company company) {
        for (Department department : company.getDepartments()) {
            department.setCompany(company);
            for (Team team : department.getTeams()) {
                team.setDepartment(department);
            }
        }

        return companyRepository.save(company);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

 }
