package org.example.tasktrackerbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TaskTrackerBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskTrackerBotApplication.class, args);
    }

}
