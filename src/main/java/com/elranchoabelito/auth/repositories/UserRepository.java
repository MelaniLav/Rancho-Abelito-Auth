package com.elranchoabelito.auth.repositories;

import com.elranchoabelito.auth.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
    Boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
}
