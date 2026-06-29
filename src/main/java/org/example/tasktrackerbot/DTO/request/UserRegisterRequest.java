package org.example.tasktrackerbot.DTO.request;

import lombok.Data;

@Data
public class UserRegisterRequest {

    private String name;
    private String username;
    private String email;
    private String password;

}
