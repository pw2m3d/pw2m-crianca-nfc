package br.com.pw2m.nfc.config;

import br.com.pw2m.nfc.entity.UserAccount;
import br.com.pw2m.nfc.entity.UserRole;
import br.com.pw2m.nfc.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final String name;

    public AdminBootstrap(
            UserAccountRepository users,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:}") String email,
            @Value("${app.admin.password:}") String password,
            @Value("${app.admin.name:Administrador PW2M}") String name
    ) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.email = email == null ? "" : email.trim().toLowerCase();
        this.password = password == null ? "" : password;
        this.name = name;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) {
            return;
        }

        UserAccount user = users.findByEmailIgnoreCase(email).orElseGet(UserAccount::new);
        boolean created = user.getId() == null;
        user.setEmail(email);
        user.setFullName(name);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(UserRole.ADMIN);
        user.setActive(true);
        users.save(user);
        log.info("Administrador {} ({})", created ? "criado" : "atualizado", email);
    }
}
