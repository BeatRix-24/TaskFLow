package com.beatrix.taskflow.service;


import com.beatrix.taskflow.dto.task.TaskCreateRequest;
import com.beatrix.taskflow.dto.task.TaskResponse;
import com.beatrix.taskflow.dto.task.TaskUpdateRequest;
import com.beatrix.taskflow.entity.Task;
import com.beatrix.taskflow.entity.User;
import com.beatrix.taskflow.exception.TaskNotFoundException;
import com.beatrix.taskflow.exception.UnauthorizedAcessException;
import com.beatrix.taskflow.exception.UserNotFoundException;
import com.beatrix.taskflow.repository.TaskRepository;
import com.beatrix.taskflow.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found with email" + email));
    }

    public TaskService(TaskRepository taskRepository, UserRepository userRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private TaskResponse mapToResponse(Task task){
        TaskResponse response = new TaskResponse();
        response.setId(task.getTaskId());
        response.setTask(task.getTask());
        response.setDone(task.getDone());
        response.setCreatedAt(task.getCreatedAt());

        return response;
    }

    public TaskResponse createTask(TaskCreateRequest request){
        User user = getCurrentUser();

        Task task = Task.builder()
                .task(request.getTask())
                .done(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        return mapToResponse(taskRepository.save(task));
    }

    public List<TaskResponse> getCurrentUserTasks(){
        User user = getCurrentUser();

        return taskRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TaskResponse markTaskDone(Integer taskId){
        User user = getCurrentUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new TaskNotFoundException("Can not find such task"));

        if(!task.getUser().getId().equals(user.getId())){
            throw new UnauthorizedAcessException("You are not authorized to modify this task");
        }
        task.setDone(true);
        return mapToResponse(taskRepository.save(task));
    }

    public void deleteTask(Integer taskId){
        User user = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new RuntimeException("Task not found"));

        if(!task.getUser().getId().equals(user.getId())){
            throw new UnauthorizedAcessException("You are not authorized to modify this task");
        }

        taskRepository.delete(task);
    }

    public TaskResponse updateTask(Integer taskId, TaskUpdateRequest request){
        User user = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new RuntimeException("Task not found"));

        if(!task.getUser().getId().equals(user.getId())){
            throw new UnauthorizedAcessException("You are not authorized to modify this task");
        }

        if(request.getTask() != null){
            task.setTask(request.getTask());
        }

        if(request.getDone() != null){
            task.setDone(request.getDone());
        }
        
        return mapToResponse(taskRepository.save(task));
    }
}
