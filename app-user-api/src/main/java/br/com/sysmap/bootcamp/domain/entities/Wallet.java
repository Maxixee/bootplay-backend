package br.com.sysmap.bootcamp.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "WALLET")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column( name = "balance" )
    private BigDecimal balance;

    @Column( name = "points")
    private Long points;

    @Column( name = "last_update")
    private LocalDateTime lastUpdate;

    @OneToOne(cascade=CascadeType.PERSIST)
    @JoinColumn( name = "USERS_ID")
    private Users users;


}
