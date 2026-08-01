package org.example.tasktrackerbot.DTO.request.signable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasktrackerbot.DTO.request.UserRegisterRequest;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterAndLinkRequest {

    private UserRegisterRequest registerRequest;

    private LinkRequest linkRequest;

}
