package org.jankowskirafal.oddamwdobrerece.institutions;

import org.jankowskirafal.oddamwdobrerece.users.AuthorityConverter;
import org.jankowskirafal.oddamwdobrerece.users.AuthorityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstitutionAdminViewController.class)
class InstitutionAdminViewControllerTest {

//    Spring Security, by default, enables CSRF (Cross-Site Request Forgery) protection.
//    This means that for POST, PUT, and DELETE requests,
//    you need to include a valid CSRF token in your requests.
//    to fix added .with(csrf())) // Add CSRF token


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstitutionService institutionService;

    @MockBean
    private AuthorityRepository authorityRepository;

    @Autowired
    private AuthorityConverter authorityConverter;

    private Institution institution1;
    private Institution institution2;

    @BeforeEach
    void setUp() {
        institution1 = new Institution();
        institution1.setInstitutionId(1L);
        institution1.setName("Institution 1");
        institution1.setDescription("Description 1");

        institution2 = new Institution();
        institution2.setInstitutionId(2L);
        institution2.setName("Institution 2");
        institution2.setDescription("Description 2");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void listInstitutions_shouldReturnInstitutionsList() throws Exception {
        Page<Institution> institutionPage = new PageImpl<>(Arrays.asList(institution1, institution2));
        when(institutionService.getAllInstitutions(anyInt(), anyInt(), anyString())).thenReturn(institutionPage);

        mockMvc.perform(get("/admin/institutions")
                        .param("page", "0")
                        .param("size", "10")
                        .param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("/institution/institutions-list"))
                .andExpect(model().attributeExists("institutions"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("search"));

        verify(institutionService).getAllInstitutions(0, 10, "test");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void showInstitutionForm_shouldReturnInstitutionForm() throws Exception {
        mockMvc.perform(get("/admin/institutions/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("/institution/institution-form"))
                .andExpect(model().attributeExists("institution"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveInstitution_withValidData_shouldRedirectToList() throws Exception {
        when(institutionService.saveInstitution(any(Institution.class))).thenReturn(institution1);

        mockMvc.perform(post("/admin/institutions/save")
                        .param("name", "New Institution")
                        .param("description", "New Description")
                        .with(csrf())) // Add CSRF token
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/institutions"))
                .andExpect(flash().attributeExists("message"));

        verify(institutionService).saveInstitution(any(Institution.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveInstitution_withInvalidData_shouldReturnFormWithErrors() throws Exception {
        mockMvc.perform(post("/admin/institutions/save")
                        .param("name", "")  // Empty name to trigger validation error
                        .param("description", "New Description")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("/institution/institution-form"))
                .andExpect(model().attributeHasFieldErrors("institution", "name"));

        verify(institutionService, never()).saveInstitution(any(Institution.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void showEditInstitutionForm_withExistingId_shouldReturnForm() throws Exception {
        when(institutionService.getInstitutionById(1L)).thenReturn(Optional.of(institution1));

        mockMvc.perform(get("/admin/institutions/edit/1")
                        .with(csrf())) // Add CSRF token
                .andExpect(status().isOk())
                .andExpect(view().name("/institution/institution-form"))
                .andExpect(model().attributeExists("institution"));

        verify(institutionService).getInstitutionById(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void showEditInstitutionForm_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(institutionService.getInstitutionById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/institutions/edit/99"))
                .andExpect(status().isNotFound());

        verify(institutionService).getInstitutionById(99L);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteInstitution_shouldRedirectToList() throws Exception {
        mockMvc.perform(post("/admin/institutions/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/institutions"))
                .andExpect(flash().attributeExists("message"));

        verify(institutionService).deleteInstitution(1L);
    }
}