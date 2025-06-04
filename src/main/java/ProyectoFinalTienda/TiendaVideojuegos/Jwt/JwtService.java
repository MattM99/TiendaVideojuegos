package ProyectoFinalTienda.TiendaVideojuegos.Jwt;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY = "4f7c2b74d4429e9292825b7c45f6c0f3ab78ed02b313b66b727f6c25e5f91dcf";

    public String getToken(UserDetails cuenta) {
        return getToken(new HashMap<>(), cuenta);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails cuenta) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(cuenta.getUsername()) // Nombre de usuario del titular de la cuenta
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de emisión del token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // 1 día de validez
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() { // Genera una clave a partir de la clave secreta
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}