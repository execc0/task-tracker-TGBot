package org.example.tasktrackerbot.DTO.request.signable;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.tasktrackerbot.DTO.request.UserLoginRequest;


@Data
@AllArgsConstructor
public class LoginAndLinkRequest {

    private UserLoginRequest loginRequest;

    private LinkRequest linkRequest;


}
