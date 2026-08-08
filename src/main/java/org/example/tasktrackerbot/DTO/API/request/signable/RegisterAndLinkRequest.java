package org.example.tasktrackerbot.DTO.API.request.signable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasktrackerbot.DTO.API.request.UserRegisterRequest;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAndLinkRequest {

    private UserRegisterRequest registerRequest;

    private LinkRequest linkRequest;

}
