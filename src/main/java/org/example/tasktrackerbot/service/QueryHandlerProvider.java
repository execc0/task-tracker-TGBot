package org.example.tasktrackerbot.service;

import org.example.tasktrackerbot.queries.Query;

import java.util.Map;

public interface QueryHandlerProvider {

    public Map<Query, QueryHandler> getQueryHandlers();

}
