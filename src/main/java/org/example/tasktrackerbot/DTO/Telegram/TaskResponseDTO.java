package org.example.tasktrackerbot.DTO.Telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasktrackerbot.DTO.API.request.Priority;
import org.example.tasktrackerbot.DTO.API.request.Status;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDTO {

    private long id;
    private String title;
    private Status status;
    private Priority priority;
    private LocalDateTime createdAt;
    private String description;

}
