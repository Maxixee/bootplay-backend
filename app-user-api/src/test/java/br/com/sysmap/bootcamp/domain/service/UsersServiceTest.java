package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.exception.EntityNotFoundException;
import br.com.sysmap.bootcamp.domain.exception.UserAlreadyExistsException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//ok
@SpringBootTest
public class UsersServiceTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private WalletService walletService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private WalletRepository walletRepository;


    @Test
    @DisplayName("Should return users when valid users is saved")
    public void shouldReturnUsersWhenValidUsersIsSaved() {
        Users users = Users.builder().id(1L).name("test").email("test@test.com").password("test").build();
        Wallet wallet = Wallet.builder().id(1L).users(users).points(0L).balance(BigDecimal.ZERO).lastUpdate(LocalDateTime.now()).build();
        when(usersRepository.save(any(Users.class))).thenReturn(users);

        Users savedUsers = usersService.save(users);

        assertEquals(users, savedUsers);
    }

    @Test
    @DisplayName("Should return an exception when the saved user already exists")
    public void shouldReturnExceptionWhenUserAlreadyExists(){
        Users user1 = Users.builder().id(null).name("test").email("test@test.com").password("test").build();
        Users user2 = Users.builder().id(null).name("test").email("test@test.com").password("test").build();
        usersRepository.save(user1);

        when(usersRepository.save(user2)).thenThrow(UserAlreadyExistsException.class);

        assertThrows(UserAlreadyExistsException.class, () -> usersRepository.save(user2));
    }


    @Test
    @DisplayName("Should return users when informed id is valid")
    public void shouldReturnUsersWhenValidIdIsInformed() {
        Users users = Users.builder().id(1L).name("test").email("test@test.com").password("test").build();
        Wallet wallet = Wallet.builder().id(1L).users(users).points(0L).balance(BigDecimal.ZERO).lastUpdate(LocalDateTime.now()).build();
        when(usersRepository.findById(users.getId())).thenReturn(Optional.of(users));

        Users foundUsers = usersService.getById(users.getId());

        assertEquals(users, foundUsers);
    }

    @Test
    @DisplayName("Should return an exception when the id reported is not valid")
    public void shouldReturnExceptionWhenInvalidIdInformed() {
        Users users = Users.builder().id(1L).name("test").email("test@test.com").password("test").build();
        Wallet wallet = Wallet.builder().id(1L).users(users).points(0L).balance(BigDecimal.ZERO).lastUpdate(LocalDateTime.now()).build();

        when(usersRepository.findById(2L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> usersRepository.findById(2L));
    }

    @Test
    @DisplayName("Should return a list of registered users")
    public void shouldReturnListOfRegisteredUsers() {
        Users user1 = Users.builder().id(1L).name("test").email("test@test.com").password("teste").build();
        Users user2 = Users.builder().id(2L).name("test1").email("test1@test.com").password("teste1").build();
        Wallet wallet1 = Wallet.builder().id(1L).users(user1).points(0L).balance(BigDecimal.ZERO).lastUpdate(LocalDateTime.now()).build();
        Wallet wallet2 = Wallet.builder().id(1L).users(user2).points(0L).balance(BigDecimal.ZERO).lastUpdate(LocalDateTime.now()).build();
        List<Users> usersList = new ArrayList<>();
        usersList.add(user1);
        usersList.add(user2);

        when(usersRepository.findAll()).thenReturn(usersList);

    }

    @Test
    @DisplayName("Should load a user")
    public void shouldLoadUser() {
        Users users = Users.builder()
                .id(1L)
                .email("test@test.com")
                .name("test")
                .password("test").build();

        when(usersRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(users));

        var loadedUser =  usersService.loadUserByUsername(users.getEmail());

        assertNotNull(loadedUser);
    }

    @Test
    @DisplayName("Should return exception when no user is loaded")
    public void shouldReturnExceptionWhenNoUserIsLoaded() {
        when(usersRepository.findByEmail(any())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> {
            usersService.loadUserByUsername("invalid@email.com");
        });
    }

    @Test
    @DisplayName("Should return users when informed email is valid")
    public void shouldReturnUsersWhenValidEmailIsInformed() {
        Users users = Users.builder().id(1L).name("test").email("test@test.com").password("test").build();
        Wallet wallet = Wallet.builder().id(1L).users(users).points(0L).balance(BigDecimal.ZERO).lastUpdate(LocalDateTime.now()).build();
        when(usersRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(users));

        Users foundUsers = usersService.findByEmail(users.getEmail());

        assertEquals(users, foundUsers);
    }

    @Test
    @DisplayName("Should return an exception when informed email is valid")
    public void shouldReturnExceptionWhenInvalidEmailIsInformed() {
        Users users = Users.builder().id(1L).name("test").email("test@test.com").password("test").build();
        Wallet wallet = Wallet.builder().id(1L).users(users).points(0L).balance(BigDecimal.ZERO).lastUpdate(LocalDateTime.now()).build();

        when(usersRepository.findByEmail("invalid@email.com")).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> usersService.findByEmail("invalid@email.com"));
    }

    @Test
    @DisplayName("Should return wallet when informed user exists")
    void shouldReturnWalletWhenUserExists() {
        Users user = Users.builder().id(1L).build();
        Wallet wallet = Wallet.builder().id(1L).balance(BigDecimal.TEN).points(0L).build();
        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWallet(user);

        assertEquals(wallet, result);
    }

    @Test
    @DisplayName("Should return exception when user does not exist")
    void shouldReturnExceptionWhenUserDoesNotExist() {
        Users user = Users.builder().id(1L).build();
        when(walletRepository.findByUsers(user)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> walletService.getWallet(user));
    }

    @Test
    @DisplayName("Should authenticate users successfully")
    public void shouldAuthenticateUsersSuccessfully() {
        AuthDto authDto = new AuthDto("test@test.com", "test", 1L, "");
        Users users = Users.builder()
                .id(1L)
                .email("test@test.com")
                .name("test")
                .password("test").build();

        when(usersRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(users));
        when(passwordEncoder.matches(authDto.getPassword(), users.getPassword())).thenReturn(true);

        assertDoesNotThrow(() -> {
            usersService.auth(authDto);
        });
    }

    @Test
    @DisplayName("Should return exception when invalid auth is provided")
    public void shouldReturnExceptionWhenInvalidAuthIsProvided() {
        AuthDto authDto = new AuthDto("test@test.com", "test", 1L, "");
        Users users = Users.builder()
                .id(1L)
                .email("test@test.com")
                .name("test")
                .password("test").build();

        when(usersRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(users));
        when(passwordEncoder.matches(authDto.getPassword(), users.getPassword())).thenReturn(false);

        var thrown = assertThrows(RuntimeException.class, () -> {
            usersService.auth(authDto);
        });

        assertEquals("Invalid password", thrown.getMessage());
    }

    @Test
    @DisplayName("Should update user successfully")
    public void shouldUpdateUserSuccessfully() {
        Users existingUser = Users.builder()
                .id(1L)
                .name("existing")
                .email("existing@test.com")
                .password("existing_password")
                .build();

        Users updatedUser = Users.builder()
                .id(1L)
                .name("updated")
                .email("updated@test.com")
                .password("updated_password")
                .build();

        when(usersRepository.findByEmail(updatedUser.getEmail())).thenReturn(Optional.empty());
        when(usersRepository.save(any(Users.class))).thenReturn(updatedUser);

        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn(updatedUser.getPassword());

        Users result = usersService.update(updatedUser);

        assertEquals(updatedUser, result);
    }

    @Test
    @DisplayName("Should return Exception when trying to update user with existing email")
    public void shouldReturnExceptionWhenTryingToUpdateUserWithExistingEmail() {
        Users existingUser = Users.builder()
                .id(1L)
                .name("existing")
                .email("existing@test.com")
                .password("existing_password")
                .build();

        Users updatedUser = Users.builder()
                .id(2L)
                .name("updated")
                .email("existing@test.com")
                .password("updated_password")
                .build();

        when(usersRepository.findByEmail(updatedUser.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> {
            usersService.update(updatedUser);
        });
    }
}
