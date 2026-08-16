package org.example.tasktrackerbot.responder;

import org.example.tasktrackerbot.DTO.API.response.PageResponseDTO;
import org.example.tasktrackerbot.DTO.API.response.TaskResponse;
import org.example.tasktrackerbot.DTO.API.response.UserResponse;
import org.example.tasktrackerbot.session.UserState;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageFormatter {

    private final String TASK_COUNT_LIMIT = "20";
    private final Integer ELEMENTS_ON_PAGE = 5;

    public String formatTaskDTO(TaskResponse taskResponse) {

        return "<b>Задача id#" + taskResponse.getId() + "</b>\n" +
                "Название: " + taskResponse.getTitle() + "\n" +
                "Описание: " + taskResponse.getDescription() + "\n" +
                "Статус: " + taskResponse.getStatus().getText() + "\n" +
                "Приоритет: " + taskResponse.getPriority().getText() + "\n\n";

    }

    public String formatTaskDTOList(List<TaskResponse> taskResponseList) {

        return taskResponseList.stream()
                .map(response -> formatTaskDTO(response))
                .reduce("", (acc, response) -> acc + response);

    }

    public String buildTaskPage(PageResponseDTO<TaskResponse> pageResponse) {
        Integer currentTaskCount = pageResponse.getContent().size();
        Integer currentPage = pageResponse.getCurrentPage();
        String header = "У вас задач: " + pageResponse.getTotalElements() + "/" + TASK_COUNT_LIMIT + '\n' +
                "Показаны задачи " + (currentPage*ELEMENTS_ON_PAGE + 1) + "-" + ((currentPage)*ELEMENTS_ON_PAGE + currentTaskCount) + "\n\n";
        String tasksFormatted = formatTaskDTOList(pageResponse.getContent());

        return header+tasksFormatted;

    }

    public String formatUserDTO(UserResponse userResponse) {

        return "<b>Пользователь</b> id#" + userResponse.getId() + "\n" +
                "<b>Имя:</b> " + userResponse.getName() + "\n" +
                "<b>Username:</b> " + userResponse.getUsername() + "\n" +
                "<b>Email:</b> " + userResponse.getEmail() + "\n";

    }

    public String buildProgressBar(UserState state) {

        final int barLength = 5;

        StringBuilder progressBar = new StringBuilder();

        int totalSteps = state.getTotalSteps();
        int currentStep = state.getCurrentStep();

        int dotsToDraw = (int) Math.round((double) currentStep/totalSteps * barLength);

        for (int i = 1; i <= barLength; i++) {
            progressBar.append(i <= dotsToDraw ? "🟩" : "⬜");
        }

        return progressBar + String.format(" Шаг %d/%d", currentStep, totalSteps);

    }

}
