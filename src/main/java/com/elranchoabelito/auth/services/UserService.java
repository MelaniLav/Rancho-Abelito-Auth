package com.elranchoabelito.auth.services;

import com.elranchoabelito.auth.models.dtos.UserDto;
import com.elranchoabelito.auth.models.entities.AppRole;
import com.elranchoabelito.auth.models.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> listUsers();

    void updateRole(String appRole, Long idUser);

    UserDto getUserById(Long id);
}
