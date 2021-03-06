package com.blog.javablogging.config.jwtconfig;
import com.blog.javablogging.model.security.oauth.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    //retrieve username form JWT token
    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }

    public  <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //For retrieving any information from token we will need the secret key
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public String refreshToken(String token) throws Exception {
        boolean validateToken = validateToken(token);
        if (validateToken){
            Claims allClaimsFromToken = getAllClaimsFromToken(token);
            if (allClaimsFromToken.isEmpty()){
                throw new Exception("Invalid token claims");
            }
            allClaimsFromToken.setIssuedAt(new Date());
            allClaimsFromToken.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000));
            return Jwts
                    .builder()
                    .setClaims(allClaimsFromToken)
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();
        }
        throw new Exception("Invalid token claims");
    }

    //Check if the token has expired
    public Boolean isTokenExpired(String token){
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //get expiration date from JWT token
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    //Generate token for user
    public String generateToken(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userPrincipal.getEmail());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean validateToken(String token) throws Exception {
        try {
            Jwts
                    .parser().setSigningKey(this.secret)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            throw new Exception("Expired or invalid Jwt Token");
        }
    }
}
