package net.security.data.microservicesocr.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Configuration
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.user.generator}")
    private String userGenerator;

    @Value("${jwt.expiration.time}")
    private String timeExpiration;

    //Funcion encargada de la generacion del Token JWT
    public String createToken(Authentication authentication){
        Long timeExpirationLong = Long.valueOf(timeExpiration);
        Algorithm algorithm = Algorithm.HMAC256(this.secretKey);
        String username = authentication.getPrincipal().toString();
        String jwtToken = JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + timeExpirationLong))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm);
        return jwtToken;
    }

    //Validar el token de acceso
    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(this.secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(this.userGenerator)
                .build();
        return verifier.verify(token);
    }

    //Extraer el username
    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }

    //Extraer un Calim especifico
    public Claim getSpecifiedClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    //Extraer todos los claims
    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}
