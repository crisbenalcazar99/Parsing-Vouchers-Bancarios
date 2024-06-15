package net.security.data.microservicesocr.Filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import net.security.data.microservicesocr.Utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


//Filtro que validara si el Token el valido
@Configuration
public class JWTTokenValidator extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    private static final Logger logger = LoggerFactory.getLogger(JWTTokenValidator.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException, AuthenticationServiceException {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if(jwtToken != null) {
                logger.info("Token validation process begins");
                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken.substring(7));
                String username = jwtUtils.extractUsername(decodedJWT);
                //auditoryDTO.setUsername(username);
                SecurityContext context = SecurityContextHolder.getContext();
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                logger.info("Token Validation Successfully");
            }

        }catch (JWTVerificationException exception){
            logger.error("Token Validation Error");

        }
        filterChain.doFilter(request, response);
    }
}
