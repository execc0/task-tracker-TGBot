package org.example.tasktrackerbot.DTO.API.request.signable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginByChatIdRequest implements Signable {

    private String chatId;

    private Long timestamp;

    private String signature;


    @Override
    public List<Object> getSignableFields() {
        return List.of(chatId, timestamp);
    }
}
