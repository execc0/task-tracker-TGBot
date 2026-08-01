package org.example.tasktrackerbot.DTO.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    private String username;
    private String password;

}
