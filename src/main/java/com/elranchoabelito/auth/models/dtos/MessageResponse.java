package com.elranchoabelito.auth.models.dtos;

import lombok.Getter;
import lombok.Setter;


public class MessageResponse {

    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
