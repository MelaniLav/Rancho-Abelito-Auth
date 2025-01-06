package com.elranchoabelito.auth.security;

import com.elranchoabelito.auth.models.entities.AppRole;
import com.elranchoabelito.auth.models.entities.Role;
import com.elranchoabelito.auth.models.entities.User;
import com.elranchoabelito.auth.repositories.RoleRepository;
import com.elranchoabelito.auth.repositories.UserRepository;
import com.elranchoabelito.auth.security.jwt.AuthEntryPointJwt;
import com.elranchoabelito.auth.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDate;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, // habilita anotaciones previas y posteriores
        securedEnabled = true, // habilita la anotacion segura
        jsr250Enabled = true //
)
public class SecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(
                AbstractHttpConfigurer::disable
        );

        http.authorizeHttpRequests( (requests) ->
                requests
                        .requestMatchers("/api/auth/public/**").permitAll()
                        .anyRequest().authenticated()
        );

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(withDefaults());


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_CLIENTE)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_CLIENTE)));

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            Role cocineroRole = roleRepository.findByRoleName(AppRole.ROLE_COCINERO)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_COCINERO)));

            Role coordinadorRole = roleRepository.findByRoleName(AppRole.ROLE_COORDINADOR)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_COORDINADOR)));

            Role asistenteVentaRole = roleRepository.findByRoleName(AppRole.ROLE_ASISTENTE_VENTA)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ASISTENTE_VENTA)));

            Role transportistaRole = roleRepository.findByRoleName(AppRole.ROLE_TRANSPORTISTA)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_TRANSPORTISTA)));

            Role asistenteEntregaRole = roleRepository.findByRoleName(AppRole.ROLE_ASISTENTE_ENTREGA)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ASISTENTE_ENTREGA)));


            if (!userRepository.existsByUserName("user")) {
                User user1 = new User("user@example.com", "user",
                        passwordEncoder.encode("password"));
                user1.setAccountNonLocked(false);
                user1.setAccountNonExpired(true);
                user1.setCredentialsNonExpired(true);
                user1.setEnable(true);
                user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
                user1.setTwoFactorEnabled(false);
                user1.setSignUpMethod("email");
                user1.setRole(userRole);
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin@example.com", "admin",
                        passwordEncoder.encode("adminPass"));
                admin.setAccountNonLocked(true);
                admin.setAccountNonExpired(true);
                admin.setCredentialsNonExpired(true);
                admin.setEnable(true);
                admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
                admin.setTwoFactorEnabled(false);
                admin.setSignUpMethod("email");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }
        };
    }

}
