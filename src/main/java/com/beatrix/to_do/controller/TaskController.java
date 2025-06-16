package com.beatrix.to_do.controller;


import com.beatrix.to_do.entity.Task;
import com.beatrix.to_do.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }
    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestParam Integer userId,
                                           @RequestParam String task){
        Task newTask = taskService.createTask(userId,task);
        return ResponseEntity.ok(newTask);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Task>> getUserTask(@PathVariable Integer userId){
        return ResponseEntity.ok(taskService.getUserTasks(userId));
    }

    @PostMapping("/{taskId}/done")
    public ResponseEntity<Task> markTaskDone(@PathVariable Integer taskId){
        return ResponseEntity.ok(taskService.markTaskDone(taskId));
    }
}
