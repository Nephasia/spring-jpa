package com.example.demo.controllers;

import com.example.demo.dtos.CompanyDto;
import com.example.demo.entities.Company;
import com.example.demo.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<Iterable<CompanyDto>> getCompanies() {
        Iterable<CompanyDto> companies = companyService.getAllCompanies();
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<Company> getCompany(@PathVariable Long companyId) {
        Optional<Company> company = companyService.getCompany(companyId);
        return company.map(ResponseEntity::ok)                          // 200
                .orElse(ResponseEntity.notFound().build());             // 404
    }

    @PostMapping
    public ResponseEntity<Void> createCompany(@RequestBody Company company) {
        if (company.getId() != null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);   // 406
        }

        try {
            Company newCompany = companyService.saveCompany(company);
            URI location = URI.create("/api/companies/" + newCompany.getId());
            return ResponseEntity.created(location).build();                // 201
        }
        catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);       // 400
        }
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<Void> updateCompany(@PathVariable Long companyId,
                                              @RequestBody Company company) {
        if (!Objects.equals(companyId, company.getId())) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);     // 409
        }

        Optional<Company> searchedCompany = companyService.getCompany(companyId);
        if (!searchedCompany.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);    // 404
        }

        try {
            companyService.saveCompany(company);
            return ResponseEntity.noContent().build();                  // 204
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();                 // 400
        }
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId) {
        Optional<Company> searchedCompany = companyService.getCompany(companyId);
        if (!searchedCompany.isPresent()){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);    // 404
        }

        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();                      // 204
    }

}
