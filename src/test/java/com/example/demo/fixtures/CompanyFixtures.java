package com.example.demo.fixtures;

import com.example.demo.entities.*;

public class CompanyFixtures {

    public static Company getSimpleCompleteCompany() {
        Manager manager = new Manager();
        manager.setName("manager-1");
        manager.setEmail("manager-email");

        Project project = new Project();
        project.setManager(manager);

        Team team = new Team();
        team.setName("team-1");
        team.setProject(project);

        Department department = new Department();
        department.setName("department-1");
        department.addTeam(team);

        Company company = new Company();
        company.setName("test-company-1");
        company.addDepartment(department);
        return company;
    }
}
