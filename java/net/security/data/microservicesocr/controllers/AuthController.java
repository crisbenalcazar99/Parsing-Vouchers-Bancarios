package net.security.data.microservicesocr.controllers;

import jakarta.validation.Valid;
import net.security.data.microservicesocr.messages.requests.AuthLoginRequest;
import net.security.data.microservicesocr.messages.requests.DeleteUserRequest;
import net.security.data.microservicesocr.messages.responses.JsonResponse;
import net.security.data.microservicesocr.services.OCRServiceImpl;
import net.security.data.microservicesocr.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OCRServiceImpl ocrService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    // Funcion encargado de loggear a los usuarios y devolver el token de accceso
    @PostMapping("/login")
    public ResponseEntity<JsonResponse> login(@Valid @RequestBody AuthLoginRequest userRequest)
            throws UsernameNotFoundException {
        return ResponseEntity.ok()
                .body(userDetailsService.loginUser(userRequest));
    }

    // Funcion encargado de la creacion de nuevos usuarios
    @PostMapping("/createUser")
    public ResponseEntity<JsonResponse> createUser(@Valid @RequestBody AuthLoginRequest authLoginRequest) {
        log.info("The creation username/password method is initialized");
        ResponseEntity<JsonResponse> responseEntity = ResponseEntity.ok()
                .body(ocrService.userCreated(authLoginRequest));
        log.info("user successfully created");
        return responseEntity;
    }

    // Funcion encargada de eliminar a los usuarios
    @DeleteMapping("/deleteUser")
    public ResponseEntity<JsonResponse> deleteUser(@Valid @RequestBody DeleteUserRequest deleteUserRequest) {
        log.info("user delete method was initialized");
        return ResponseEntity.ok()
                .body(ocrService.deleteUser(deleteUserRequest));
    }
}
