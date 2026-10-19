package br.com.pw2m.nfc.security;

import br.com.pw2m.nfc.entity.UserAccount;
import br.com.pw2m.nfc.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserAccountRepository users;

    public CustomUserDetailsService (
            UserAccountRepository users
    ) {
        this.users = users;
    }


    @Override
    public UserDetails loadUserByUsername (
            String username
    ) throws UsernameNotFoundException {

        UserAccount user = users.findByEmailIgnoreCase (username)
                .orElseThrow (() -> new UsernameNotFoundException (
                                "Usuário não encontrado."
                        )
                );
        return User
                .withUsername (user.getEmail ())
                .password (user.getPasswordHash ())
                .authorities ("ROLE_" + user.getRole ().name ())
                .disabled (!user.isActive ())
                .build ();
    }

}