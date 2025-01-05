package com.elranchoabelito.auth.repositories;

import com.elranchoabelito.auth.models.entities.AppRole;
import com.elranchoabelito.auth.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}
