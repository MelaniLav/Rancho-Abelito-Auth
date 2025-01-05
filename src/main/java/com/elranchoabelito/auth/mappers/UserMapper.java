package com.elranchoabelito.auth.mappers;

import com.elranchoabelito.auth.models.dtos.UserDto;
import com.elranchoabelito.auth.models.entities.User;

public class UserMapper {

    public static UserDto toUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setIdUser(user.getIdUser());
        userDto.setEmail(user.getEmail());
        userDto.setUserName(user.getUserName());
        userDto.setPassword(user.getPassword());
        userDto.setAccountNonLocked(user.isAccountNonLocked());
        userDto.setAccountNonExpired(user.isAccountNonExpired());
        userDto.setCredentialsNonExpired(user.isCredentialsNonExpired());
        userDto.setEnable(user.isEnable());
        userDto.setCredentialsExpiryDate(user.getCredentialsExpiryDate());
        userDto.setAccountExpiryDate(user.getAccountExpiryDate());
        userDto.setTwoFactorSecret(user.getTwoFactorSecret());
        userDto.setTwoFactorEnabled(user.isTwoFactorEnabled());
        userDto.setSingUpMethod(user.getSignUpMethod());
        userDto.setRoleId(userDto.getRoleId());
        userDto.setCreatedDate(user.getCreatedDate());
        userDto.setUpdatedDate(user.getUpdatedDate());

        return userDto;
    }
}
