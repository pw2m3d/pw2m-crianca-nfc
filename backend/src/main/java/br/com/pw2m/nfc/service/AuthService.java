package br.com.pw2m.nfc.service;

import br.com.pw2m.nfc.dto.AuthDtos;
import br.com.pw2m.nfc.entity.UserAccount;
import br.com.pw2m.nfc.exception.ConflictException;
import br.com.pw2m.nfc.repository.UserAccountRepository;
import br.com.pw2m.nfc.security.CustomUserDetailsService;
import br.com.pw2m.nfc.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            UserAccountRepository users,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (users.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Este e-mail já está cadastrado.");
        }

        UserAccount user = new UserAccount();
        user.setFullName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        users.save(user);

        UserDetails details = userDetailsService.loadUserByUsername(email);
        String token = jwtService.generateToken(details);

        return response(user, token);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        UserAccount user = users.findByEmailIgnoreCase(email)
                .orElseThrow();

        UserDetails details = userDetailsService.loadUserByUsername(email);
        String token = jwtService.generateToken(details);

        return response(user, token);
    }

    private AuthDtos.AuthResponse response(UserAccount user, String token) {
        return new AuthDtos.AuthResponse(
                token,
                new AuthDtos.UserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail()
                )
        );
    }
}
