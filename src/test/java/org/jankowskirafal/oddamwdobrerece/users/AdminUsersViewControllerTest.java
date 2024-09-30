package org.jankowskirafal.oddamwdobrerece.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import org.jankowskirafal.oddamwdobrerece.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(AdminUsersViewController.class)
@Import(SecurityConfig.class)
class AdminUsersViewControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private AuthorityServiceImpl authorityServiceImpl;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userServiceImpl;

    @MockBean
    private AuthorityRepository authorityRepository;

    @Autowired
    private AuthorityConverter authorityConverter;

    private User testUser;
    private Page<User> userPage;
    Authority adminAuthority;
    Authority userAuthority;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("testuser");

        List<User> userList = new ArrayList<>();
        userList.add(testUser);

        userPage = new PageImpl<>(userList);

        adminAuthority = new Authority();
        adminAuthority.setId(1L);
        adminAuthority.setName("ROLE_ADMIN");

        userAuthority = new Authority();
        userAuthority.setId(2L);
        userAuthority.setName("ROLE_USER");

        when(authorityRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminAuthority));
        when(authorityRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userAuthority));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListUsers() throws Exception {
        when(userServiceImpl.getAllUsersByRoleName(0, 10, "ROLE_USER")).thenReturn(userPage);

        mockMvc.perform(get("/admin/users")
                        .param("search", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/users-list"))
                .andExpect(model().attributeExists("users", "currentPage", "search"));
    }

    @Test
    void testAuthorityConverter() {

        Authority authority = new Authority();
        authority.setId(1L);
        authority.setName("ROLE_ADMIN");

        String authorityName = "ROLE_ADMIN";
        when(authorityRepository.findByName(authorityName)).thenReturn(Optional.of(authority));
        Authority convertedAuthority = authorityConverter.convert(authorityName);

        assertNotNull(convertedAuthority);
        assertEquals(authorityName, convertedAuthority.getName());
        assertEquals(1L, convertedAuthority.getId());
        verify(authorityRepository, times(1)).findByName(authorityName);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDisplayAddUserForm() throws Exception {

        List<Authority> authorities = new ArrayList<>();
        authorities.add(userAuthority);

        when(authorityServiceImpl.getAllAuthorities()).thenReturn(authorities);

        mockMvc.perform(get("/admin/users/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin_add_user_form"))
                .andExpect(model().attributeExists("user", "allAuthorities"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveUser() throws Exception {

        mockMvc.perform(post("/admin/users/save")
                        .with(csrf())
                        .param("email", "newuser@example.com")
                        .param("password", "password")
                        .param("active", "true")
                        .param("authorities","ROLE_USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userServiceImpl, times(1)).createUser(any(User.class), anySet());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser() throws Exception {
        mockMvc.perform(post("/admin/users/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attributeExists("message"));

        verify(userServiceImpl, times(1)).deleteUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDisplayEditUserForm() throws Exception {
        when(userServiceImpl.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(authorityServiceImpl.getAllAuthorities()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin_add_user_form"))
                .andExpect(model().attributeExists("user", "allAuthorities"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminUsers() throws Exception {
        when(userServiceImpl.getAllUsersByRoleName(0, 10, "ROLE_ADMIN")).thenReturn(userPage);

        mockMvc.perform(get("/admin/admins")
                        .param("search", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admins-list"))
                .andExpect(model().attributeExists("admins", "currentPage", "search"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDisplayAddAdminForm() throws Exception {

        List<Authority> authorities = new ArrayList<>();
        authorities.add(adminAuthority);
        when(authorityServiceImpl.getAllAuthorities()).thenReturn(authorities);

        mockMvc.perform(get("/admin/admins/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin_add_form"))
                .andExpect(model().attributeExists("user", "allAuthorities"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveAdminWithAuthorities() throws Exception {

        mockMvc.perform(post("/admin/admins/save")
                        .with(csrf())
                        .param("email", "newadmin@example.com")
                        .param("password", "password")
                        .param("active", "true")
                        .param("authorities", "ROLE_ADMIN", "ROLE_USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admins"));

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Set> authoritiesArgumentCaptor = ArgumentCaptor.forClass(Set.class);
        verify(userServiceImpl, times(1)).createUser(userArgumentCaptor.capture(), authoritiesArgumentCaptor.capture());

        User user = userArgumentCaptor.getValue();
        assertThat(user.getEmail()).isEqualTo("newadmin@example.com");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getPassword()).isNotEmpty();

        Set capturedAuthorities = authoritiesArgumentCaptor.getValue();

        assertThat(authoritiesArgumentCaptor.getValue()).hasSize(2);

        assertThat(capturedAuthorities).containsAll(Set.of(userAuthority.getName(), adminAuthority.getName()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDisplayEditAdminForm() throws Exception {
        when(userServiceImpl.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(authorityServiceImpl.getAllAuthorities()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/admins/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/admin_add_form"))
                .andExpect(model().attributeExists("user", "allAuthorities"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteAdmin() throws Exception {
        mockMvc.perform(post("/admin/admins/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admins"))
                .andExpect(flash().attributeExists("message"));

        verify(userServiceImpl, times(1)).deleteUserById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }


}