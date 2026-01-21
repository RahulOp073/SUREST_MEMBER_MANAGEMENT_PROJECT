package org.surest.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private Algorithm algorithm;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    void init() {
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
    }

    public String generateToken(String username, List<String> roles) {
        return JWT.create()
                .withSubject(username)
                .withArrayClaim("roles", roles.toArray(new String[0]))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMs()))
                .withIssuer(jwtProperties.getIssuer())
                .sign(algorithm);
    }

    public DecodedJWT decodeToken(String token) {
        return JWT.require(algorithm)
                .withIssuer(jwtProperties.getIssuer())
                .build()
                .verify(token);
    }

    public String extractUsername(String token) {
        return decodeToken(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        String[] roles = decodeToken(token)
                .getClaim("roles")
                .asArray(String.class);

        if (roles != null && roles.length > 0) {
            return Arrays.asList(roles);
        }

        String role = decodeToken(token).getClaim("role").asString();
        return role != null ? List.of(role) : Collections.emptyList();
    }

    public boolean validateToken(String token, String username) {
        try {
            DecodedJWT decoded = decodeToken(token);
            return username.equals(decoded.getSubject()) && !isTokenExpired(decoded);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isTokenExpired(DecodedJWT decodedJWT) {
        return decodedJWT.getExpiresAt().before(new Date());
    }
}
