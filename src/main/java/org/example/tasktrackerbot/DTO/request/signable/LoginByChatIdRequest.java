package org.example.tasktrackerbot.DTO.request.signable;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginByChatIdRequest implements Signable {

    private String chatId;

    private Long timestamp;

    private String signature;


    @Override
    public List<Object> getSignableFields() {
        return List.of(chatId, timestamp);
    }
}
