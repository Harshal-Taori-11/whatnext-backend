package com.whatnext.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "task_code", length = 5)
    private String taskCode;

    @Column(name = "task_number")
    private Integer taskNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String status = "new"; // new, in_progress, done, archived, removed

    @Column(length = 20)
    private String priority = "none"; // none, low, medium, high

    @Column(length = 20)
    private String category = "other"; // work, home, family, routine, health, finance, personal, other

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(nullable = false)
    private Integer position = 0;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "task_tags",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // Auto-set completed_at when status changes to done
        if ("done".equals(status) && completedAt == null) {
            completedAt = LocalDateTime.now();
        }

        // Auto-set archived_at when archived
        if (isArchived != null && isArchived && archivedAt == null) {
            archivedAt = LocalDateTime.now();
        }
    }

    // Helper method to get full task ID
    @Transient
    public String getFullTaskId() {
        if (taskCode != null && taskNumber != null) {
            return taskCode + "-" + taskNumber;
        }
        return null;
    }
}
