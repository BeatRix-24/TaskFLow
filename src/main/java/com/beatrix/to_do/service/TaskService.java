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

    public Task createTask(Integer userId, String taskDiscription){
        User user = getCurrentUser();

        Task task = Task.builder()
                .task(taskDiscription)
                .user(user)
                .build();

        return taskRepository.save(task);
    }

    public List<Task> getUserTasks(Integer userId){
        User user =getCurrentUser();
        return taskRepository.findByUserId(user.getId());
    }

    public Task markTaskDone(Integer taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(()-> new RuntimeException("Task not found"));
        task.setDone(true);
        return taskRepository.save(task);
    }
}
