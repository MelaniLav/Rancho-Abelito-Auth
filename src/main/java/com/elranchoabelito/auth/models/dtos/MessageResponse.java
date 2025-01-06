package com.elranchoabelito.auth.models.dtos;

import lombok.Getter;
import lombok.Setter;


public class MessageResponse {

    private String message;
    private String status;

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
