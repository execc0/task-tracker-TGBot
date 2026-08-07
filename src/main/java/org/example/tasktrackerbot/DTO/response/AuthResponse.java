package org.example.tasktrackerbot.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    @ToString.Exclude
    private String token;

}
