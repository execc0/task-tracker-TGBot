package org.example.tasktrackerbot.DTO.API.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {

    private String name;
    private String username;
    private String email;
    @ToString.Exclude
    private String password;

}
