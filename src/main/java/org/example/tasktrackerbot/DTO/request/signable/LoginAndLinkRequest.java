package org.example.tasktrackerbot.DTO.request.signable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasktrackerbot.DTO.request.UserLoginRequest;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginAndLinkRequest {

    private UserLoginRequest loginRequest;

    private LinkRequest linkRequest;


}
