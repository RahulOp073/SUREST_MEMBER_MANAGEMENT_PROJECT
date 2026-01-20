package org.surest.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "my_super_secret_key_which_should_be_long_and_secure";
    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 10;
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    public String generateToken(String username, List<String> roles) {
        return JWT.create()
                .withSubject(username)
                .withArrayClaim("roles", roles.toArray(new String[0]))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .withIssuer("example.com")
                .sign(algorithm);
    }

    public DecodedJWT decodeToken(String token) {
        return JWT.require(algorithm)
                .withIssuer("example.com")
                .build()
                .verify(token);
    }

    public String extractUsername(String token) {
        return decodeToken(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        String[] arr = decodeToken(token).getClaim("roles").asArray(String.class);
        if (arr != null && arr.length > 0) {
            return Arrays.asList(arr);
        }
        String role = decodeToken(token).getClaim("role").asString();
        if (role != null) {
            return List.of(role);
        }
        return Collections.emptyList();
    }

    public boolean validateToken(String token, String username) {
        try {
            DecodedJWT decoded = decodeToken(token);
            return decoded.getSubject().equals(username) && !isTokenExpired(decoded);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }
}
