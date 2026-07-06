package org.example.tasktrackerbot.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnlinkSocialRequest {

    private String username;

    private String password;

    private String provider;

    private String providerId;

}
