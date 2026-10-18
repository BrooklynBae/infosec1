
package com.example.infosec1.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public final class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm)
                .withIssuer("infosec1")
                .build();
    }

    public String generateToken(String username) {
        Instant now = Instant.now();

        return JWT.create()
                .withIssuer("infosec1")
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(1, ChronoUnit.HOURS))
                .sign(algorithm);
    }

    public String extractUsername(String token) {
        return verifier.verify(token).getSubject();
    }
}
