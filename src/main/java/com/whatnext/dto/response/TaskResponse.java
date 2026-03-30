package com.whatnext.dto.response;

import com.whatnext.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String taskCode;
    private Integer taskNumber;
    private String fullTaskId; // e.g., "JHDOE-1"
    private String title;
    private String description;
    private String status;
    private String priority;
    private String category;
    private LocalDate dueDate;
    private Integer position;
    private Boolean isArchived;
    private LocalDateTime completedAt;
    private LocalDateTime archivedAt;
    private Set<TagResponse> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TaskResponse fromEntity(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTaskCode(task.getTaskCode());
        response.setTaskNumber(task.getTaskNumber());
        response.setFullTaskId(task.getFullTaskId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setCategory(task.getCategory());
        response.setDueDate(task.getDueDate());
        response.setPosition(task.getPosition());
        response.setIsArchived(task.getIsArchived());
        response.setCompletedAt(task.getCompletedAt());
        response.setArchivedAt(task.getArchivedAt());
        response.setTags(task.getTags().stream()
            .map(TagResponse::fromEntity)
            .collect(Collectors.toSet()));
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }
}
