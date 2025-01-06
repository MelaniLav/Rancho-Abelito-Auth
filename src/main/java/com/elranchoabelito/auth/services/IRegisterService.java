package com.elranchoabelito.auth.services;

import com.elranchoabelito.auth.models.dtos.MessageResponse;
import com.elranchoabelito.auth.models.dtos.SignupRequest;

public interface IRegisterService {

    MessageResponse registerUser(SignupRequest signupRequest);

}
