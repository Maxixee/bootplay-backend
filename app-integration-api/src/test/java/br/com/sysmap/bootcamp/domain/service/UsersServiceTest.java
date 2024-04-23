package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.exception.EntityNotFoundException;
import br.com.sysmap.bootcamp.domain.respository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//ok
@SpringBootTest
class UsersServiceTest {

    @Autowired
    private UsersService usersService;

    @MockBean
    private UsersRepository usersRepository;

    @Test
    @DisplayName("Should load a user")
    public void shouldLoadUser() {
        Users users = Users.builder()
                .id(1L)
                .email("test@test.com")
                .password("test").build();

        when(usersRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(users));

        var loadedUser =  usersService.loadUserByUsername(users.getEmail());

        assertNotNull(loadedUser);
    }


    @Test
    @DisplayName("Should return users when informed email is valid")
    public void shouldReturnUsersWhenValidEmailIsInformed() {
        Users users = Users.builder()
                .id(1L)
                .email("test@test.com")
                .password("test").build();
        when(usersRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(users));

        Users foundUsers = usersService.findByEmail(users.getEmail());

        assertEquals(users, foundUsers);
    }

    @Test
    @DisplayName("Should return an exception when the email reported is not valid")
    public void shouldReturnExceptionWhenInvalidEmailInformed() {
        Users users = Users.builder()
                .id(1L)
                .email("test@test.com")
                .password("test").build();

        when(usersRepository.findByEmail("invalid@test.com")).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> usersRepository.findByEmail("invalid@test.com"));
    }
}