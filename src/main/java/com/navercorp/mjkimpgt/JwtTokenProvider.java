package com.navercorp.mjkimpgt;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {
	@Value(value = "spring.jwt.secret")
	private String jwtKey;
	
	private long tokenValid = 1000L * 60 * 60;
	private final UserService userService = new UserService();
	
	@PostConstruct
    protected void init() {
        jwtKey = Base64.getEncoder().encodeToString(jwtKey.getBytes());
    }
	
	public String createToken(String privateKey, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(privateKey);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValid))
                .signWith(SignatureAlgorithm.HS256, jwtKey)
                .compact();
    }
	
	public Authentication getAuthentication(String token) {
        UserDetails userDetails = userService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
	
	public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token).getBody().getSubject();
    }

	public boolean validateToken(String jwtToken) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(jwtToken);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

}
