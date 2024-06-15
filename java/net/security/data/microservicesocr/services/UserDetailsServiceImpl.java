package net.security.data.microservicesocr.services;

import jakarta.persistence.EntityNotFoundException;
import net.security.data.microservicesocr.Utils.JwtUtils;
import net.security.data.microservicesocr.enums.MnemonicsEnum;
import net.security.data.microservicesocr.messages.requests.AuthLoginRequest;
import net.security.data.microservicesocr.models.DTOS.AuditoryDTO;
import net.security.data.microservicesocr.models.DTOS.CustomUserDetails;
import net.security.data.microservicesocr.models.entities.CatalogEntity;
import net.security.data.microservicesocr.models.entities.UserEntity;
import net.security.data.microservicesocr.messages.responses.JsonResponse;
import net.security.data.microservicesocr.repository.CatalogRepository;
import net.security.data.microservicesocr.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuditoryDTO auditoryDTO;
    @Autowired
    private CatalogRepository catalogRepository;

    // Proceso de looging y obtencion del token de un usuario
    public JsonResponse loginUser(AuthLoginRequest authLoginRequest)
            throws UsernameNotFoundException, BadCredentialsException, DisabledException{
        log.info("The username/password authentication method is initialized");
        String username = authLoginRequest.getUsername();
        String password = authLoginRequest.getPassword();

        Authentication authentication = this.authenticationMethod(username, password);
        log.info("Successful user/password authentication");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        CatalogEntity catalogEntity = catalogRepository.findCatalogEntityByMnemonic(MnemonicsEnum.SUCCESSFUL_AUTHENTICATION.getValue())
                .orElseThrow(() -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
        auditoryDTO.setCatalogEntity(catalogEntity);
        auditoryDTO.setResponse(catalogEntity.getDescription());
        auditoryDTO.setRequest(MnemonicsEnum.CUSTOMER_LOGIN_INFORMATION.getValue());
        log.info("Successfully generated token for user: {}", username);
        return new JsonResponse(true, HttpStatus.OK, "User logged successfully", accessToken);
    }

    //Se valida el usuario, contrssena  enviados para el proceso de loggin

    public Authentication authenticationMethod(String username, String password)
            throws UsernameNotFoundException, BadCredentialsException, DisabledException{
        UserDetails userDetails = this.loadUserByUsername(username);

        //Se valida que la cuenta se encuentre habilitada
        if(!userDetails.isEnabled()){
            log.error("User Account is Disabled: {}", username);
            throw new DisabledException("User Account is Disabled: " + username);
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password for user: " + username);
        }
        return new UsernamePasswordAuthenticationToken(username, password);
    }

    //Obtiene el registro del usuario de la base de datos
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authentication error: Invalid username : " + username));
        return new CustomUserDetails(userEntity);
    }


}
