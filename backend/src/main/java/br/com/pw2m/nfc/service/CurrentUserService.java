package br.com.pw2m.nfc.service;

import br.com.pw2m.nfc.entity.UserAccount;
import br.com.pw2m.nfc.repository.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserAccountRepository users;

    public CurrentUserService(UserAccountRepository users) {
        this.users = users;
    }

    public UserAccount require(Authentication authentication) {
        return users.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow();
    }
}
