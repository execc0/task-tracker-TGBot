package org.example.tasktrackerbot.DTO.API.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tasktrackerbot.DTO.API.request.Priority;
import org.example.tasktrackerbot.DTO.API.request.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private long id;
    private String title;
    private Status status;
    private Priority priority;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponse user;

}
