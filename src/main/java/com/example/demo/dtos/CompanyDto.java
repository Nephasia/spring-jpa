package com.example.demo.dtos;

import com.example.demo.entities.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String name;

    public static CompanyDto fromEntity(Company company) {
        return new CompanyDto(company.getId(), company.getName());
    }

}
