package com.beatrix.taskflow.dto.task;

import lombok.Data;

@Data
public class TaskUpdateRequest {
    private String task;
    private Boolean done;
}
