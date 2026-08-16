package org.example.tasktrackerbot.DTO.API.request;

import lombok.Getter;

public enum Priority {

    LOW("LOW \uD83D\uDFE2"),
    MEDIUM("MEDIUM ⚠️"),
    HIGH("HIGH \uD83D\uDD25");

    @Getter
    private final String text;

    Priority(String text) {

        this.text = text;

    }

}
