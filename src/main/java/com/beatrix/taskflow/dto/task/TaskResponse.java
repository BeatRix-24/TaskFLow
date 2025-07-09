package com.beatrix.taskflow.dto.task;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse {
    private Integer id;
    private String task;
    private boolean done;
    private LocalDateTime createdAt;
}
