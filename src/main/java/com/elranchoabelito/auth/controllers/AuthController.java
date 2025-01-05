package com.elranchoabelito.auth.controllers;

import com.elranchoabelito.auth.models.dtos.LoginRequest;
import com.elranchoabelito.auth.models.dtos.LoginResponse;
import com.elranchoabelito.auth.models.dtos.MessageResponse;
import com.elranchoabelito.auth.models.dtos.SignupRequest;
import com.elranchoabelito.auth.models.entities.AppRole;
import com.elranchoabelito.auth.models.entities.Role;
import com.elranchoabelito.auth.models.entities.User;
import com.elranchoabelito.auth.repositories.RoleRepository;
import com.elranchoabelito.auth.repositories.UserRepository;
import com.elranchoabelito.auth.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    private final Logger log = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(jwtToken, userDetails.getUsername(), roles);

        return ResponseEntity.ok(response);
    }


    @PostMapping(value = "/public/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("Signup request received: {}", signupRequest);

        if (userRepository.existsByUserName(signupRequest.getUsername())) {
            log.warn("Username already exists: {}", signupRequest.getUsername());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            log.warn("Email already exists: {}", signupRequest.getEmail());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        log.info("Creating new user...");
        User user = new User(
                signupRequest.getEmail(), signupRequest.getUsername(),
                encoder.encode(signupRequest.getPassword()));

        log.info("Assigning roles...");
        Set<String> strRoles = signupRequest.getRole();
        Role role;
        if (strRoles == null || strRoles.isEmpty()) {
            role = roleRepository.findByRoleName(AppRole.ROLE_CLIENTE)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            String roleStr = strRoles.iterator().next();
            if (roleStr.equals("admin")) {
                role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            } else {
                role = roleRepository.findByRoleName(AppRole.ROLE_CLIENTE)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            }

            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setEnable(true);
            user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            user.setAccountExpiryDate(LocalDate.now().plusYears(1));
            user.setTwoFactorEnabled(false);
            user.setSignUpMethod("email");
        }
        log.info("Ya vamos a guardar ... {}", user.getIdUser());
        user.setRole(role);
        log.info("Le asigno este rol ... {}", user.getRole().getRoleName());
        log.info("El enable del orto... {}", user.isEnable());
        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUserName());

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
