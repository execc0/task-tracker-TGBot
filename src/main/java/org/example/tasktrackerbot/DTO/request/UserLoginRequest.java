package org.example.tasktrackerbot.DTO.request;


import lombok.Data;

@Data
public class UserLoginRequest {

    private String username;
    private String password;

}
