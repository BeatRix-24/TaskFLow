package com.beatrix.to_do.service;


import com.beatrix.to_do.entity.Task;
import com.beatrix.to_do.entity.User;
import com.beatrix.to_do.repository.TaskRepository;
import com.beatrix.to_do.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
    }

    public TaskService(TaskRepository taskRepository, UserRepository userRepository){
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(String taskDescription){
        User user = getCurrentUser();

        Task task = Task.builder()
                .task(taskDescription)
                .user(user)
                .build();

        return taskRepository.save(task);
    }

    public List<Task> getCurrentUserTasks(){
        User user = getCurrentUser();
        return taskRepository.findByUserId(user.getId());
    }

    public Task markTaskDone(Integer taskId){
        User user = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new RuntimeException("Task not found"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to modify this task");
        }
        
        task.setDone(true);
        return taskRepository.save(task);
    }
}
