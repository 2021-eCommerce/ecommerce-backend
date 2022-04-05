package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.enums.AccountRole;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AccountRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter @Accessors(chain = true)
    public static class RegisterRequest implements Serializable{
        private String email;
        private String passwordHash;
        private String name;
        @Enumerated(EnumType.STRING)
        private AccountRole accountRole;
        private List<AddressRequest.RegisterAddress> address = new ArrayList<>();

        public Account toAccount(){
            return Account.builder()
                    .email(email)
                    .passwordHash(new BCryptPasswordEncoder().encode(passwordHash))
                    .name(name)
                    .accountRole(accountRole)
                    .lastLogin(LocalDateTime.now())
                    .addressList(new ArrayList<>())
                    .build();
        }
    }

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest implements Serializable {
        private String email;
        private String passwordHash;
    }

    @Getter @Setter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String email;

        private String passwordHash;

        private String new_passwordHash;

        private String name;
    }
}