package org.example.tasktrackerbot.DTO.API.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private long id;
    private String name;
    private String email;
    private String username;

}
