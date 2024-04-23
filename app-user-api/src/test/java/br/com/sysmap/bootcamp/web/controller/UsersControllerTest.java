package br.com.sysmap.bootcamp.web.controller;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should return a user when a valid user is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() throws Exception {
        Users users = Users.builder()
                .email("test@test.com")
                .name("test")
                .password("test").build();

        when(usersService.save(any(Users.class))).thenReturn(users);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(users)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }


    @Test
    @DisplayName("Should return a user when a valid user ID is provided")
    public void shouldReturnUserWhenValidUserIdProvided() throws Exception {
        Users user = Users.builder()
                .id(1L)
                .email("test@test.com")
                .name("test")
                .password("test").build();

        when(usersService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    @DisplayName("Should return a list of users when there are users in the system")
    public void shouldReturnListOfUsersWhenUsersExist() throws Exception {
        Users user1 = Users.builder()
                .id(1L)
                .email("test1@test.com")
                .name("test1")
                .password("test1").build();

        Users user2 = Users.builder()
                .id(2L)
                .email("test2@test.com")
                .name("test2")
                .password("test2").build();

        List<Users> userList = Arrays.asList(user1, user2);

        when(usersService.getAll()).thenReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("test1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("test2")));
    }

    @Test
    @DisplayName("Should update user successfully")
    public void shouldUpdateUserSuccessfully() throws Exception {
        Users updatedUser = Users.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .password("test")
                .build();

        when(usersService.getUserByContext()).thenReturn(updatedUser);
        when(usersService.update(updatedUser)).thenReturn(null);

        mockMvc.perform(put("/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated"));

        verify(usersService).update(updatedUser);
    }

}

