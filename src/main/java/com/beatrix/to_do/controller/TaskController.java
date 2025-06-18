package com.beatrix.to_do.controller;


import com.beatrix.to_do.dto.task.TaskCreateRequest;
import com.beatrix.to_do.dto.task.TaskResponse;
import com.beatrix.to_do.dto.task.TaskUpdateRequest;
import com.beatrix.to_do.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;


    @PostMapping("/create-task")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskCreateRequest request){
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks(){
        return ResponseEntity.ok(taskService.getCurrentUserTasks());
    }

    @PostMapping("/{taskId}/mark-done")
    public ResponseEntity<TaskResponse> markDone(@PathVariable Integer taskId){
        return ResponseEntity.ok(taskService.markTaskDone(taskId));
    }

    @DeleteMapping("/delete-task/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Integer taskId){
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @PutMapping("/update-task/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Integer taskId,
                                           @RequestBody TaskUpdateRequest request){
        return ResponseEntity.ok(taskService.updateTask(taskId,request));
    }
}
