package br.com.pw2m.nfc.service;

import br.com.pw2m.nfc.exception.TooManyRequestsException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttemptLimiterTest {

    @Test
    void bloqueiaAposCincoFalhas() {
        AttemptLimiter limiter = new AttemptLimiter();
        String key = "claim:127.0.0.1:ana@pw2m.com.br";

        for (int i = 0; i < 5; i++) {
            limiter.guard(key);
            limiter.recordFailure(key);
        }

        assertThatThrownBy(() -> limiter.guard(key))
                .isInstanceOf(TooManyRequestsException.class);
    }

    @Test
    void resetLiberaChave() {
        AttemptLimiter limiter = new AttemptLimiter();
        String key = "login:127.0.0.1:ana@pw2m.com.br";
        for (int i = 0; i < 5; i++) {
            limiter.recordFailure(key);
        }
        limiter.reset(key);
        limiter.guard(key);
    }
}
