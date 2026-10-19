package br.com.pw2m.nfc.service;

import br.com.pw2m.nfc.exception.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AttemptLimiter {

    private static final int MAX_FAILURES = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);

    private final ConcurrentHashMap<String, Window> attempts = new ConcurrentHashMap<>();

    public void guard(String key) {
        Window window = attempts.get(key);
        if (window == null) {
            return;
        }
        if (window.expiresAt.isBefore(Instant.now())) {
            attempts.remove(key);
            return;
        }
        if (window.failures >= MAX_FAILURES) {
            throw new TooManyRequestsException(
                    "Muitas tentativas. Aguarde alguns minutos e tente novamente."
            );
        }
    }

    public void recordFailure(String key) {
        Instant now = Instant.now();
        attempts.compute(key, (ignored, current) -> {
            if (current == null || current.expiresAt.isBefore(now)) {
                return new Window(1, now.plus(WINDOW));
            }
            return new Window(current.failures + 1, current.expiresAt);
        });
    }

    public void reset(String key) {
        attempts.remove(key);
    }

    private record Window(int failures, Instant expiresAt) {}
}
