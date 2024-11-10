package com.example.demo.controllers;

import com.example.demo.BaseTest;
import com.example.demo.fixtures.CompanyFixtures;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.repositories.CompanyServiceObjectMother;
import com.example.demo.services.CompanyService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class CompanyControllerTest extends BaseTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private CompanyRepository companyRepository;
    private DepartmentRepository departmentRepository;
    private ProjectRepository projectRepository;
    private TeamRepository teamRepository;
    private ManagerRepository managerRepository;
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyRepository = CompanyRepositoryObjectMother.getDefault();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        companyService = CompanyServiceObjectMother.getDefault();
        departmentRepository = DepartmentRepositoryObjectMother.getDefault();
        projectRepository = ProjectRepositoryObjectMother.getDefault();
        teamRepository = TeamRepositoryObjectMother.getDefault();
        managerRepository = ManagerRepositoryObjectMother.getDefault();

        companyRepository.deleteAll();
    }

    @Test
    public void getCompanies_whenNoPresent_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void getCompanies_whenMultipleCreated_returnsThem() throws Exception {
        Company company = new Company();
        company.setName("test-company-1");
        companyRepository.save(company);

        Company company2 = new Company();
        company2.setName("test-company-2");
        companyRepository.save(company2);

        ResultActions resultActions = mockMvc.perform(get("/api/companies"));
        MvcResult result = resultActions.andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Company> companies = objectMapper.readValue(body, new TypeReference<>(){});

        assertNotNull(companies);
        assertEquals(2, companies.size());
    }

    @Test
    public void getCompanies_whenCompleteStructure_returnsOnlyCompanies() throws Exception {
        Company company = CompanyFixtures.getSimpleCompleteCompany();
        companyRepository.save(company);

        MvcResult result = mockMvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Company> companies = objectMapper.readValue(body, new TypeReference<>(){});

        assertNotNull(companies);
        assertEquals(1, companies.size());
        assertTrue(companies.get(0).getDepartments().isEmpty());
    }

    @Test
    public void getCompany_whenCompleteStructure_returnsAll() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        String uri = "/api/companies/" + savedCompany.getId();
        MvcResult result = mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Company company = objectMapper.readValue(body, new TypeReference<>(){});

        assertNotNull(company);
        assertFalse(company.getDepartments().isEmpty());
        assertEquals("department-1", company.getDepartments().get(0).getName());
        assertEquals("team-1", company.getDepartments().get(0).getTeams().get(0).getName());
        assertNotNull(company.getDepartments().get(0).getTeams().get(0).getProject().getId());
        assertEquals("manager-1", company.getDepartments().get(0).getTeams().get(0).getProject().getManager().getName());
    }

    @Test
    public void getCompany_whenNotExisting_returnsNotFound() throws Exception {
        String uri = "/api/companies/1";
        mockMvc.perform(get(uri))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createCompany_whenCompleteCompany_createsIt() throws Exception {
        Iterable<Company> companies = companyRepository.findAll();
        assertEquals(0, Lists.newArrayList(companies).size());

        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String postUri = "/api/companies";
        MvcResult mvcResult = mockMvc.perform(post(postUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        Long createdId = Long.parseLong(location.replace("/api/companies/", ""));

        Optional<Company> optionalCompany = companyService.getCompany(createdId);
        Company company = optionalCompany.get();

        assertNotNull(company);
        assertFalse(company.getDepartments().isEmpty());
        assertEquals("department-1", company.getDepartments().get(0).getName());
        assertEquals("team-1", company.getDepartments().get(0).getTeams().get(0).getName());
        assertNotNull(company.getDepartments().get(0).getTeams().get(0).getProject().getId());
        assertEquals("manager-1", company.getDepartments().get(0).getTeams().get(0).getProject().getManager().getName());
    }

    @Test
    public void createCompany_whenJustCompany_createsIt() throws Exception {
        Optional<Company> missingCompany = companyRepository.findById(1L);
        assertFalse(missingCompany.isPresent());

        Company companyToCreate = new Company();
        companyToCreate.setName("only-company");

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String postUri = "/api/companies";
        MvcResult mvcResult = mockMvc.perform(post(postUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        Long createdId = Long.parseLong(location.replace("/api/companies/", ""));

        Optional<Company> optionalCompany = companyService.getCompany(createdId);
        Company company = optionalCompany.get();

        assertNotNull(company);
        assertTrue(company.getDepartments().isEmpty());
    }

    @Test
    public void createCompany_whenCompanyMissingName_doesNotCreateAnyEntity() throws Exception {
        Optional<Company> missingCompany = companyRepository.findById(1L);
        assertFalse(missingCompany.isPresent());

        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        companyToCreate.setName(null);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String postUri = "/api/companies";
        mockMvc.perform(post(postUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest());

        Optional<Company> company = companyService.getCompany(1L);
        assertFalse(company.isPresent());
        Iterable<Department> all = departmentRepository.findAll();
        assertEquals(0, Lists.newArrayList(all).size());
    }

    @Test
    public void createCompany_whenCreated_thenLocationInHeaderIsPresent() throws Exception {
        Optional<Company> missingCompany = companyRepository.findById(1L);
        assertFalse(missingCompany.isPresent());

        Company companyToCreate = new Company();
        companyToCreate.setName("only-company");

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String postUri = "/api/companies";
        mockMvc.perform(post(postUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/companies/\\d+")));
    }

    @Test
    public void createCompany_whenIdSetOnCompany_thenNotAcceptable() throws Exception {

        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        companyToCreate.setId(123456L);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String postUri = "/api/companies";
        mockMvc.perform(post(postUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void createCompany_whenIdSetOnDepartment_thenNotAcceptable() throws Exception {

        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        companyToCreate.getDepartments().get(0).setId(12345L);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String postUri = "/api/companies";
        mockMvc.perform(post(postUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createCompany_whenIdSetOnTeam_thenBadRequest() throws Exception {

        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        companyToCreate.getDepartments().get(0).getTeams().get(0).setId(12345L);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String postUri = "/api/companies";
        mockMvc.perform(post(postUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateCompany_whenCompanyExists_thenGetsUpdated() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        companyToCreate.setName("new-company-name");

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String putUri = "/api/companies/" + savedCompany.getId();
        mockMvc.perform(put(putUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNoContent());

        Optional<Company> company = companyService.getCompany(savedCompany.getId());
        assertTrue(company.isPresent());
        assertEquals("new-company-name", company.get().getName());
        assertEquals(companyToCreate.getDepartments().get(0).getName(),
                company.get().getDepartments().get(0).getName());
    }

    @Test
    public void updateCompany_whenManagerIsUpdated_thenGetsUpdated() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        companyToCreate.getDepartments().get(0).getTeams().get(0).getProject().getManager().setName("new-manager-name");

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String putUri = "/api/companies/" + savedCompany.getId();
        mockMvc.perform(put(putUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNoContent());

        Optional<Company> company = companyService.getCompany(savedCompany.getId());
        assertTrue(company.isPresent());
        assertEquals(companyToCreate.getDepartments().get(0).getTeams().get(0).getProject().getManager().getName(),
                company.get().getDepartments().get(0).getTeams().get(0).getProject().getManager().getName());
    }

    @Test
    public void updateCompany_whenTeamIsAdded_thenGetsUpdated() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        Team newTeam = new Team();
        newTeam.setName("new-team");
        companyToCreate.getDepartments().get(0).addTeam(newTeam);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyToCreate );

        String putUri = "/api/companies/" + savedCompany.getId();
        mockMvc.perform(put(putUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNoContent());

        Optional<Company> company = companyService.getCompany(savedCompany.getId());
        assertTrue(company.isPresent());
        List<Team> teams = companyToCreate.getDepartments().get(0).getTeams();
        assertEquals(2, teams.size());
        assertEquals("team-1", teams.get(0).getName());
        assertEquals("new-team", teams.get(1).getName());
    }

    @Test
    public void updateCompany_whenUrlIdNotMatchBodyId_thenConflict() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        Company companyUpdate = CompanyFixtures.getSimpleCompleteCompany();
        companyUpdate.setName("new-company-name");
        companyUpdate.setId(234L);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyUpdate );
        String putUri = "/api/companies/" + savedCompany.getId();
        mockMvc.perform(put(putUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isConflict());
    }

    @Test
    public void updateCompany_whenIdMissingInBody_thenConflict() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        Company companyUpdate = CompanyFixtures.getSimpleCompleteCompany();
        companyUpdate.setName("new-company-name");
        companyUpdate.setId(null);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyUpdate );
        String putUri = "/api/companies/" + savedCompany.getId();
        mockMvc.perform(put(putUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isConflict());
    }

    @Test
    public void updateCompany_whenNameMissingInBody_thenConflict() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        Company companyUpdate = CompanyFixtures.getSimpleCompleteCompany();
        companyUpdate.setName(null);
        companyUpdate.setId(savedCompany.getId());

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequestBody = mapper.writeValueAsString( companyUpdate );
        String putUri = "/api/companies/" + savedCompany.getId();
        mockMvc.perform(put(putUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCompany_whenExists_deletes() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        String deleteUri = "/api/companies/" + savedCompany.getId();
        mockMvc.perform(delete(deleteUri))
                .andExpect(status().isNoContent());

        Optional<Company> missingCompany = companyRepository.findById(savedCompany.getId());
        assertFalse(missingCompany.isPresent());

        Iterable<Department> departments = departmentRepository.findAll();
        assertEquals(0, Lists.newArrayList(departments).size());

        Iterable<Project> projects = projectRepository.findAll();
        assertEquals(0, Lists.newArrayList(projects).size());

        Iterable<Team> teams = teamRepository.findAll();
        assertEquals(0, Lists.newArrayList(teams).size());

        Iterable<Manager> managers = managerRepository.findAll();
        assertEquals(0, Lists.newArrayList(managers).size());
    }

    @Test
    public void deleteCompany_whenNotExists_returnsNotFound() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        String deleteUri = "/api/companies/0";
        mockMvc.perform(delete(deleteUri))
                .andExpect(status().isNotFound());

        Iterable<Department> departments = departmentRepository.findAll();
        assertEquals(1, Lists.newArrayList(departments).size());
    }

    @Test
    public void getCompany_whenPassedStringAsId_thenBadRequest() throws Exception {
        String uri = "/api/companies/abc";
        mockMvc.perform(get(uri))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateCompany_whenIssueInJson_thenBadRequest() throws Exception {
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        String putUri = "/api/companies/" + savedCompany.getId();

        String jsonRequestBody = "{\"id\":1,\"name\":\"new-company-name\",\"departments\":[{\"id\":1,\"name\":\"department-1\",\"teams\":\"string_here\"}]}";
        mockMvc.perform(put(putUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createCompany_whenIssueInJson_thenBadRequest() throws Exception {
        String jsonRequestBody = "{\"name\":\"new-company-name\",\"departments\":[{\"name\":\"department-1\",\"teams\":\"string_here\"}]}";
        String postUri = "/api/companies";
        mockMvc.perform(post(postUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCompany_whenPassedStringAsId_thenBadRequest() throws Exception {
        String uri = "/api/companies/abc";
        mockMvc.perform(delete(uri))
                .andExpect(status().isBadRequest());
    }

    // reassign the department to new company
    @Test
    public void updateCompany_whenReassignDepartmentToNewCompany_thenGetsUpdated() throws Exception {
        // initial company with department creation
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        // fetching department details that will be moved
        String uri = "/api/companies/" + savedCompany.getId();
        MvcResult result = mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Company company = objectMapper.readValue(body, new TypeReference<>(){});

        assertFalse(company.getDepartments().isEmpty());
        assertEquals("department-1", company.getDepartments().get(0).getName());

        // new company preparation with no department
        Company newCompany = new Company();
        newCompany.setName("new-company");
        Company newSavedCompany = companyRepository.save(newCompany);
        assertNotNull(newSavedCompany.getId());

        // reassigned department to new company
        newCompany.addDepartment(company.getDepartments().get(0));

        String jsonRequestBody = objectMapper.writeValueAsString( newCompany );

        String putUri = "/api/companies/" + newSavedCompany.getId();
        mockMvc.perform(put(putUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNoContent());

        Optional<Company> updatedNewCompany = companyService.getCompany(newSavedCompany.getId());
        assertTrue(updatedNewCompany.isPresent());
        assertEquals("new-company", updatedNewCompany.get().getName());
        assertEquals("department-1", updatedNewCompany.get().getDepartments().get(0).getName());

        // verify whether department have been removed from the origin company
        MvcResult result2 = mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andReturn();
        String body2 = result2.getResponse().getContentAsString();
        Company company2 = objectMapper.readValue(body2, new TypeReference<>(){});
        assertTrue(company2.getDepartments().isEmpty());
    }

    @Test
    public void updateCompany_whenDepartmentRemoved_thenGetsUpdated() throws Exception {
        // initial company with department creation
        Company companyToCreate = CompanyFixtures.getSimpleCompleteCompany();
        Company savedCompany = companyRepository.save(companyToCreate);
        assertNotNull(savedCompany.getId());

        // fetching department details
        String uri = "/api/companies/" + savedCompany.getId();
        MvcResult result = mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Company company = objectMapper.readValue(body, new TypeReference<>(){});

        assertFalse(company.getDepartments().isEmpty());
        assertEquals("department-1", company.getDepartments().get(0).getName());

        // removing the department
        savedCompany.getDepartments().remove(0);

        String jsonRequestBody = objectMapper.writeValueAsString( savedCompany );

        String putUri = "/api/companies/" + savedCompany.getId();
        mockMvc.perform(put(putUri).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(status().isNoContent());

        Optional<Company> updatedNewCompany = companyService.getCompany(savedCompany.getId());
        assertTrue(updatedNewCompany.isPresent());
        assertEquals("test-company-1", updatedNewCompany.get().getName());
        assertEquals(0, updatedNewCompany.get().getDepartments().size());

        // verify whether department have been removed from the origin company
        MvcResult result2 = mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andReturn();
        String body2 = result2.getResponse().getContentAsString();
        Company company2 = objectMapper.readValue(body2, new TypeReference<>(){});
        assertTrue(company2.getDepartments().isEmpty());
    }

}