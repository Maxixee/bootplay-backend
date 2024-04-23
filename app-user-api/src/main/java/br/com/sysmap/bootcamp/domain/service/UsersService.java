package br.com.sysmap.bootcamp.domain.service;


import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.exception.EntityNotFoundException;
import br.com.sysmap.bootcamp.domain.exception.InvalidRegistrationInformationException;
import br.com.sysmap.bootcamp.domain.exception.UserAlreadyExistsException;
import br.com.sysmap.bootcamp.domain.repository.UsersRepository;
import br.com.sysmap.bootcamp.domain.repository.WalletRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UsersService implements UserDetailsService {
    private final UsersRepository usersRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRED)
    public Users save(Users user) {

        Optional<Users> usersOptional = this.usersRepository.findByEmail(user.getEmail());
        if(usersOptional.isPresent()){
            throw new UserAlreadyExistsException("User already exists");
        }
        if(user.getName().isBlank() || user.getEmail().isBlank() || user.getPassword().isBlank()){
            throw new InvalidRegistrationInformationException("Invalid registration credentials (Name, User or Password)");
        }


        user = user.toBuilder().password(this.passwordEncoder.encode(user.getPassword())).build();

        Users userEntity = this.usersRepository.save(user);

        this.createWallet(user);

        log.info("Saving user: {}", user);
        return userEntity;
    }

    @Transactional(readOnly = true)
    public Users getById(Long id) {
        return usersRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário id=%s não encontrado", id))
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users update(Users user) {
        Optional<Users> usersOptional = this.usersRepository.findByEmail(user.getEmail());
        if (usersOptional.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        Users newUser = Users.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .build();

        return this.usersRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> usersOptional = this.usersRepository.findByEmail(username);

        return usersOptional.map(users -> new User(users.getEmail(), users.getPassword(), new ArrayList<GrantedAuthority>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
    }

    public Users findByEmail(String email){
        return this.usersRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
    }

    public AuthDto auth(AuthDto authDto) {
        Users users = this.findByEmail(authDto.getEmail());

        if (!this.passwordEncoder.matches(authDto.getPassword(), users.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        StringBuilder password = new StringBuilder().append(users.getEmail()).append(":").append(users.getPassword());

        return AuthDto.builder().email(users.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.toString().getBytes())
        ).id(users.getId()).build();
    }


    //I think that this method should be in the walletService class, but this always caused a cyclical situation, so I created it in the usersService
    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet createWallet(Users user){

        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .points(0L)
                .lastUpdate(LocalDateTime.now())
                .users(user)
                .build();

        return this.walletRepository.save(wallet);
    }

    public Users getUserByContext(){
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Users users = findByEmail(email);

        return users;
    }
}
