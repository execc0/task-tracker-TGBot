package org.example.tasktrackerbot.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnlinkSocialRequest {

    private String username;
    @ToString.Exclude
    private String password;

    private String provider;

    private String providerId;

}
