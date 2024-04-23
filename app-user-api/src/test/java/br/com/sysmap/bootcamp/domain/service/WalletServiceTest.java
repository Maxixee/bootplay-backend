package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.exception.EntityNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//ok
@SpringBootTest
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UsersService usersService;

    @MockBean
    private WalletRepository walletRepository;

    private static final Map<DayOfWeek, Long> POINTS_PER_DAY = new HashMap<>();
    static {
        POINTS_PER_DAY.put(DayOfWeek.SUNDAY, 25L);
        POINTS_PER_DAY.put(DayOfWeek.MONDAY, 7L);
        POINTS_PER_DAY.put(DayOfWeek.TUESDAY, 6L);
        POINTS_PER_DAY.put(DayOfWeek.WEDNESDAY, 2L);
        POINTS_PER_DAY.put(DayOfWeek.THURSDAY, 10L);
        POINTS_PER_DAY.put(DayOfWeek.FRIDAY, 15L);
        POINTS_PER_DAY.put(DayOfWeek.SATURDAY, 20L);
    }



    @Test
    @DisplayName("Should add balance to the wallet when user and wallet are valid")
    public void shouldAddBalanceWhenUserAndWalletAreValid() {
        Users user = Users.builder().id(1L).build();
        Wallet wallet = Wallet.builder().id(1L).balance(BigDecimal.TEN).build();

        when(walletRepository.findByUsers(user)).thenReturn(Optional.of(wallet));

        BigDecimal creditAmount = BigDecimal.valueOf(5);
        walletService.creditWallet(creditAmount, user);

        Optional<Wallet> mockedWallet = walletRepository.findByUsers(user);
        assertTrue(mockedWallet.isPresent());
        BigDecimal expectedBalance = BigDecimal.TEN.add(creditAmount);
        assertEquals(expectedBalance, mockedWallet.get().getBalance());
    }

    @Test
    @DisplayName("Should return wallet when informed user exists")
    void shouldReturnWalletWhenUserIsValid() {
        Users users = Users.builder().id(1L).name("test").email("test@test.com").password("test").build();
        Wallet wallet = Wallet.builder().id(1L).users(users).points(0L).balance(BigDecimal.ZERO).lastUpdate(LocalDateTime.now()).build();
        when(walletRepository.findByUsers(wallet.getUsers())).thenReturn(Optional.of(wallet));

        Wallet foundWallet = walletService.getWallet(wallet.getUsers());

        assertEquals(wallet, foundWallet);
    }

    @Test
    @DisplayName("Should return an exception when the id reported is not valid")
    void shouldReturnExceptiontWhenUserIsInvalid() {
        Users users = Users.builder().id(1L).name("test").email("test@test.com").password("test").build();
        Wallet wallet = Wallet.builder().id(1L).users(users).points(0L).balance(BigDecimal.ZERO).lastUpdate(LocalDateTime.now()).build();

        when(walletRepository.findByUsers(wallet.getUsers())).thenThrow(new EntityNotFoundException("Entity not found"));
    }

    @Test
    void calculatePoints_ShouldReturnCorrectPointsForCurrentDay() {
        MockitoAnnotations.openMocks(this);


        DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();
        Long expectedPoints = POINTS_PER_DAY.getOrDefault(currentDay, 0L);

        Long calculatedPoints = walletService.calculatePoints();

        assertEquals(expectedPoints, calculatedPoints);
    }
}