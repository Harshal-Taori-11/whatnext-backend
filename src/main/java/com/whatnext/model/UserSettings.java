package com.whatnext.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "auto_archive_days", nullable = false)
    private Integer autoArchiveDays = 2;

    @Column(name = "auto_archive_enabled", nullable = false)
    private Boolean autoArchiveEnabled = true;

    @Column(name = "default_priority", length = 20)
    private String defaultPriority = "none";

    @Column(name = "default_category", length = 20)
    private String defaultCategory = "other";

    @Column(name = "default_due_date", length = 20)
    private String defaultDueDate = "none";

    @Column(length = 20)
    private String theme = "auto";

    @Column(name = "theme_color", length = 20)
    private String themeColor = "blue"; // blue, pink, green, yellow, black, red

    @Column(name = "notifications_enabled", nullable = false)
    private Boolean notificationsEnabled = false;

    @Column(name = "daily_summary_time")
    private LocalTime dailySummaryTime = LocalTime.of(9, 0);

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
