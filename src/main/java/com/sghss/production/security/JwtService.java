package com.sghss.production.security;

import java.sql.Date;

import org.springframework.stereotype.Service;

import com.sghss.production.entity.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

    private static final String SECRET_KEY = "vidaplus-chave-super-secreta";

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
            .setSubject(usuario.getUsername())
            .claim("role", usuario.getRole().name())
            .setIssuedAt(new java.util.Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 6)) // 6h
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

    public String extrairEmail(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean tokenValido(String token, Usuario usuario) {
        String email = extrairEmail(token);
        return email.equals(usuario.getEmail()) && !expirado(token);
    }

    public boolean expirado(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody()
                .getExpiration().before(new java.util.Date());
    }
}


