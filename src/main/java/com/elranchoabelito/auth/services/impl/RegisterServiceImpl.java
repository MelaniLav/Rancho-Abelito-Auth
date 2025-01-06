package com.elranchoabelito.auth.services.impl;

import com.elranchoabelito.auth.controllers.AuthController;
import com.elranchoabelito.auth.models.dtos.ClienteData;
import com.elranchoabelito.auth.models.dtos.MessageResponse;
import com.elranchoabelito.auth.models.dtos.SignupRequest;
import com.elranchoabelito.auth.models.entities.AppRole;
import com.elranchoabelito.auth.models.entities.Role;
import com.elranchoabelito.auth.models.entities.User;
import com.elranchoabelito.auth.repositories.RoleRepository;
import com.elranchoabelito.auth.repositories.UserRepository;
import com.elranchoabelito.auth.services.IRegisterService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Set;

@Service
public class RegisterServiceImpl implements IRegisterService {

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RestTemplate restTemplate;

    private final Logger log = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Override
    @Transactional
    public MessageResponse registerUser(SignupRequest signupRequest) {

        /*if (userRepository.existsByUserName(signupRequest.getUsername())) {
            log.warn("Username already exists: {}", signupRequest.getUsername());
            return new MessageResponse("Error: Username is already taken!", "error");
        }

         */

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            log.warn("Email already exists: {}", signupRequest.getEmail());
            return new MessageResponse("Error: Email is already in use!","error");
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
        User userSaved = userRepository.save(user);
        ClienteData clienteData = invokeClienteService(signupRequest,userSaved.getIdUser());
        log.info("User registered successfully: {}", user.getUserName());

        return new MessageResponse("User registered successfully!","success");
    }


    private ClienteData invokeClienteService(SignupRequest signupRequest, Long cuentaId) {
        String serviceCliente = "http://localhost:8091/cliente/create";

        signupRequest.getClienteData().setCuentaId(cuentaId);
        signupRequest.getClienteData().setEmail(signupRequest.getEmail());

        try {
            ResponseEntity<ClienteData> response = restTemplate.postForEntity(serviceCliente, signupRequest.getClienteData(), ClienteData.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to create resource on Service B");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error communicating with Service B: " + e.getMessage(), e);
        }
    }
}
