package br.com.sysmap.bootcamp.web.controller;

import br.com.sysmap.bootcamp.domain.entities.Users;
import br.com.sysmap.bootcamp.domain.entities.Wallet;
import br.com.sysmap.bootcamp.domain.service.UsersService;
import br.com.sysmap.bootcamp.domain.service.WalletService;
import br.com.sysmap.bootcamp.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "Wallet", description = "Wallet API")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UsersService usersService;

    @Operation(summary = "My Wallet", description = "Feature that presents the user with their wallet",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Wallet.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Resource not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    public ResponseEntity<Wallet> getWallet() {
        Users users = usersService.getUserByContext();

        return ResponseEntity.ok(walletService.getWallet(users));
    }

    @Operation(summary = "Credit value in wallet", description = "Feature that allows users to add credits to their wallet",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Credits added successfully to wallet",
                            content = @Content(mediaType = "text/plain")),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping("/credit/{value}")
    public ResponseEntity<String> creditWallet(@PathVariable BigDecimal value) {
        Users users = usersService.getUserByContext();
        walletService.creditWallet(value, users);
        return ResponseEntity.ok("Credits added successfully to wallet");
    }

}
