package org.example.tasktrackerbot.DTO.request.signable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public interface Signable {

    @JsonIgnore
    List<Object> getSignableFields();

}
