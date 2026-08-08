package org.example.tasktrackerbot.DTO.API.request.signable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasktrackerbot.DTO.API.request.UserLoginRequest;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginAndLinkRequest {

    private UserLoginRequest loginRequest;

    private LinkRequest linkRequest;


}
