package com.example.drone.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
            "TestJwtSecretKeyForUnitTesting256Bits!!", 7200, 604800);

    @Test
    void shouldGenerateAndValidateAccessToken() {
        String token = jwtTokenProvider.generateAccessToken(1L,
                java.util.Collections.singletonList("admin"),
                java.util.Arrays.asList("drone:create", "drone:delete"));
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(1L, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals("admin", jwtTokenProvider.getRolesFromToken(token).get(0));
        assertTrue(jwtTokenProvider.getPermissionsFromToken(token).contains("drone:create"));
    }

    @Test
    void shouldRejectInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void shouldGenerateRefreshToken() {
        String token = jwtTokenProvider.generateRefreshToken(1L, false);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(1L, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void shouldGenerateRememberMeToken() {
        String token = jwtTokenProvider.generateRefreshToken(1L, true);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }
}
