package org.example.tasktrackerbot.DTO.API.request;

import lombok.Getter;

public enum Status {

    TODO("TODO ⏳"),
    IN_PROGRESS("IN_PROGRESS \uD83D\uDD04"),
    DONE("DONE ✅");

    @Getter
    private final String text;

    Status(String text) {
        this.text = text;
    }

}
