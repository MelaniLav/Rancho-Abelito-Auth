package com.elranchoabelito.auth.models.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
public class SignupRequest {

    private String username;

    private String email;

    private String password;

    @Setter
    @Getter
    private Set<String> role;


}
