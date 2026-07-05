package org.example.tasktrackerbot.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkSocialRequestPayload {

    private String provider;

    private String providerId;

    private Long timestamp;

}
