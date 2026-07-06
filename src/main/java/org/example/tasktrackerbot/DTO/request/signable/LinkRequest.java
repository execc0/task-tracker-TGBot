package org.example.tasktrackerbot.DTO.request.signable;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LinkRequest implements Signable {

    private String provider;

    private String providerId;

    private Long timestamp;

    private String signature;

    @Override
    public List<Object> getSignableFields() {
        return List.of(provider, providerId, timestamp);
    }
}
