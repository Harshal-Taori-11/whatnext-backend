package com.whatnext.controller;

import com.whatnext.dto.request.CreateTaskRequest;
import com.whatnext.dto.request.TaskFilterRequest;
import com.whatnext.dto.request.UpdateTaskRequest;
import com.whatnext.dto.response.MessageResponse;
import com.whatnext.dto.response.TaskListResponse;
import com.whatnext.dto.response.TaskResponse;
import com.whatnext.security.UserPrincipal;
import com.whatnext.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<TaskListResponse> getFilteredTasks(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @ModelAttribute TaskFilterRequest filter) {
        TaskListResponse response = taskService.getFilteredTasks(currentUser.getId(), filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        TaskResponse response = taskService.getTask(currentUser.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse response = taskService.updateTask(currentUser.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTask(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        taskService.deleteTask(currentUser.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String status) {
        List<TaskResponse> response = taskService.getTasksByStatus(currentUser.getId(), status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<TaskResponse> archiveTask(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        TaskResponse response = taskService.archiveTask(currentUser.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<TaskResponse> restoreTask(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        TaskResponse response = taskService.restoreTask(currentUser.getId(), id);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove task (soft delete - sets status to 'removed')
     */
    @PatchMapping("/{id}/remove")
    public ResponseEntity<TaskResponse> removeTask(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        TaskResponse response = taskService.removeTask(currentUser.getId(), id);
        return ResponseEntity.ok(response);
    }
}
