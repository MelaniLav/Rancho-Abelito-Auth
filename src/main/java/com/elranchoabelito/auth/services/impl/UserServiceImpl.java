package com.elranchoabelito.auth.services.impl;

import com.elranchoabelito.auth.mappers.UserMapper;
import com.elranchoabelito.auth.models.dtos.UserDto;
import com.elranchoabelito.auth.models.entities.AppRole;
import com.elranchoabelito.auth.models.entities.Role;
import com.elranchoabelito.auth.models.entities.User;
import com.elranchoabelito.auth.repositories.RoleRepository;
import com.elranchoabelito.auth.repositories.UserRepository;
import com.elranchoabelito.auth.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<User> listUsers(){
        return (List<User>) userRepository.findAll();
    }

    @Override
    public void updateRole(String appRole, Long idUser){
        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found"));
        AppRole nameRole = AppRole.valueOf(appRole);
        Role role = roleRepository.findByRoleName(nameRole).orElseThrow(
                () -> new RuntimeException("Role not found")
        );
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return UserMapper.toUserDto(user);
    }




}
