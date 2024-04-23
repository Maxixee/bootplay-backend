package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.exception.EntityNotFoundException;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final UsersService usersService;
    private final WalletRepository walletRepository;

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

    public void debit(WalletDto walletDto) {
        Users users = usersService.findByEmail(walletDto.getEmail());
        Wallet wallet = walletRepository.findByUsers(users).orElseThrow(
                () -> new EntityNotFoundException("User not found"));

        wallet.setBalance(wallet.getBalance().subtract(walletDto.getValue()));

        wallet.setPoints(wallet.getPoints() + calculatePoints());
        wallet.setLastUpdate(LocalDateTime.now());

        walletRepository.save(wallet);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void creditWallet(BigDecimal value, Users user) {
        Wallet wallet = this.walletRepository.findByUsers(user).orElseThrow(
                () -> new EntityNotFoundException("User not found"));


        wallet.setBalance(wallet.getBalance().add(value));
        walletRepository.save(wallet);
    }


    @Transactional(readOnly = true)
    public Wallet getWallet(Users user){
        return this.walletRepository.findByUsers(user).orElseThrow(
                () -> new EntityNotFoundException("User not found"));
    }

    public Long calculatePoints() {
        DayOfWeek day = LocalDateTime.now().getDayOfWeek();
        Long points = POINTS_PER_DAY.getOrDefault(day, 0L);

        return points;
    }

}
