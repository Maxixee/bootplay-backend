package br.com.sysmap.bootcamp.web.controller;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = WalletController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @MockBean
    private UsersService usersService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should return user's wallet when requested")
    public void shouldReturnUsersWalletWhenRequested() throws Exception {
        Users user = Users.builder()
                .id(1L)
                .email("test@test.com")
                .name("test")
                .password("password").build();
        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(BigDecimal.valueOf(100.00))
                .points(10L)
                .lastUpdate(LocalDateTime.now())
                .users(user)
                .build();

        Mockito.when(usersService.getUserByContext()).thenReturn(user);
        Mockito.when(walletService.getWallet(user)).thenReturn(wallet);

        mockMvc.perform(get("/wallet")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wallet.getId()))
                .andExpect(jsonPath("$.balance").value(wallet.getBalance().doubleValue()))
                .andExpect(jsonPath("$.points").value(wallet.getPoints().intValue()));
    }


    @Test
    @DisplayName("Should add credits to user's wallet when requested")
    public void shouldAddCreditsToUsersWalletWhenRequested() throws Exception {
        Users user = Users.builder()
                .id(1L)
                .email("test@test.com")
                .name("test")
                .password("password").build();

        Mockito.when(usersService.findByEmail(Mockito.anyString())).thenReturn(user);

        mockMvc.perform(post("/wallet/credit/{value}", BigDecimal.valueOf(50.00))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Credits added successfully to wallet"));
    }
}
