package org.example.tasktrackerbot.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkSocialRequest {

    private LinkSocialRequestPayload payload;

    private String signature;

}
