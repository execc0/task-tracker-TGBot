package org.example.tasktrackerbot.responder;

import org.example.tasktrackerbot.DTO.API.response.TaskResponse;
import org.example.tasktrackerbot.DTO.API.response.UserResponse;
import org.example.tasktrackerbot.DTO.Telegram.TaskResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageFormatter {

    public String formatTaskDTO(TaskResponse taskResponse) {

        return "Задача id#" + taskResponse.getId() + "\n" +
                "Название: " + taskResponse.getTitle() + "\n" +
                "Описание: " + taskResponse.getDescription() + "\n" +
                "Статус: " + taskResponse.getStatus() + "\n" +
                "Приоритет: " + taskResponse.getPriority() + "\n\n";

    }

    public String formatTaskDTOList(List<TaskResponse> taskResponseList) {

        return taskResponseList.stream()
                .map(response -> formatTaskDTO(response))
                .reduce("", (acc, response) -> acc + response);

    }

    public String formatUserDTO(UserResponse userResponse) {

        return "\uD83C\uDD94 <b>Пользователь id#</b>" + userResponse.getId() + "\n" +
                "\uD83D\uDCDD <b>Имя:</b> " + userResponse.getName() + "\n" +
                "\uD83D\uDD17 <b>Username:</b> " + userResponse.getUsername() + "\n" +
                "\uD83D\uDCE7 <b>Email:</b> " + userResponse.getEmail() + "\n";

    }

}
