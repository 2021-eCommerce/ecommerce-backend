package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalQueryService implements UserDetailsService {
    private final AccountQueryService accountQueryService;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final Account account = accountQueryService.readByEmail(email);

        return new User(account.getEmail(), account.getPasswordHash(), new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public Account readByPrincipal(Principal principal) {
        final String email = principal.getName();

        return accountQueryService.readByEmail(email);
    }
}