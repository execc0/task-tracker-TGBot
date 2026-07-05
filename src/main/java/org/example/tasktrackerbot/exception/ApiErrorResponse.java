package org.example.tasktrackerbot.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiErrorResponse(

        Integer status,
        String message,    // Для большинства исключений
        List<String> errors // Для случая со списком исключений

) {}
