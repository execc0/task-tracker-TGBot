package org.example.tasktrackerbot.responder;

import org.example.tasktrackerbot.DTO.API.response.TaskResponse;
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

}
