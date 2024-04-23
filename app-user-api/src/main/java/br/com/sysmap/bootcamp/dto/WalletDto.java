package br.com.sysmap.bootcamp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class WalletDto implements Serializable {

    private String email;
    private BigDecimal value;
}
