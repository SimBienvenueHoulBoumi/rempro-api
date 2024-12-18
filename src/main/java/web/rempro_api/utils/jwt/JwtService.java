package web.rempro_api.utils.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	String SECRET_KEY = "dvgjbhkj551dzafrevc2663q2ds1d4e522sf5ss52dfd54df5qdffff";

	public String getToken(UserDetails user) {
		return getToken(new HashMap<>(), user);
	}

	private String getToken(Map<String, Object> extraClaims, UserDetails user) {
		return Jwts.builder()
				.claims(extraClaims)
				.subject(user.getUsername()) // Nom de l'utilisateur comme sujet du token
				.issuedAt(new Date(System.currentTimeMillis())) // Date de création du token
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // Expiration après 24h
				.signWith(getKey()) // Signature du token
				.compact();
	}

	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String getUsernameFromToken(String token) {
		return getClaim(token, Claims::getSubject);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private Claims getAllClaims(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
	}

	public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Date getExpiration(String token) {
		return getClaim(token, Claims::getExpiration);
	}

	private boolean isTokenExpired(String token) {
		return getExpiration(token).before(new Date());
	}
}
