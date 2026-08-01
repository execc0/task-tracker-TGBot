package org.example.tasktrackerbot.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateRequest {

    private String title;
    private String description;
    private String priority;
    private String status;

}
