package ProyectoFinalTienda.TiendaVideojuegos.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "u8hS0qKfRjKWXPtY5v7K+TyrrY0Oqk6P5JvK/MEU5nM=";
    public String getToken(UserDetails cuenta) {
        return getToken(new HashMap<>(), cuenta);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails cuenta) {
        extraClaims.put("role", cuenta.getAuthorities().stream().findFirst().get().getAuthority());

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

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject); // Obtiene el nombre de usuario del token
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token); // Obtiene el nombre de usuario del token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Verifica si el token es válido y no ha expirado
    }

    private Claims getClaims(String token) { // Extrae las reclamaciones del token, que contienen información del usuario y otros datos
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims); // Aplica la función para obtener un valor específico de las reclamaciones
    }

    private Date getExpirationDateFromToken(String token) {
        return getClaim(token, Claims::getExpiration); // Obtiene la fecha de expiración del token
    }

    private Boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date()); // Verifica si el token ha expirado
    }

    public Claims getAllClaims(String token) {
        return getClaims(token);
    }

}