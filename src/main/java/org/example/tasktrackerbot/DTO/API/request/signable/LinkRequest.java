package org.example.tasktrackerbot.DTO.API.request.signable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
