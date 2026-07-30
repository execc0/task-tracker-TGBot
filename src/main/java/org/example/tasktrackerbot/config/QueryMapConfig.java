package org.example.tasktrackerbot.config;

import lombok.extern.slf4j.Slf4j;
import org.example.tasktrackerbot.queries.CallbackQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class QueryMapConfig {

    @Bean(name = "botQueryMap")
    public Map<String, CallbackQuery> createBotQueryMap(List<CallbackQuery> queryList) {

        log.debug("queryList: {}", queryList);
        log.debug("Вызван метод createBotQueryMap");
        Map<String, CallbackQuery> map = queryList.stream()
                .collect(Collectors.toMap(query -> query.getQuery(), query -> query));
        log.debug("После заполнения мапа: {}", map.keySet());
        return map;
    }

}
