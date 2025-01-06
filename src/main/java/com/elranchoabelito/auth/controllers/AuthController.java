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
import com.elranchoabelito.auth.services.IRegisterService;
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
    private IRegisterService registerService;

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
    public ResponseEntity<MessageResponse>  registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        MessageResponse messageResponse = registerService.registerUser(signupRequest);

        HttpStatus status = messageResponse.getStatus().equals("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(messageResponse, status);
    }

}
