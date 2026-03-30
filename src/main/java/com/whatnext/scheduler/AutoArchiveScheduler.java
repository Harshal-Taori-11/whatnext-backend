package com.whatnext.scheduler;

import com.whatnext.model.Task;
import com.whatnext.model.User;
import com.whatnext.model.UserSettings;
import com.whatnext.repository.TaskRepository;
import com.whatnext.repository.UserRepository;
import com.whatnext.repository.UserSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AutoArchiveScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AutoArchiveScheduler.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Runs every day at 2 AM to archive old completed tasks
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void archiveOldCompletedTasks() {
        logger.info("Starting auto-archive job");

        List<User> users = userRepository.findAll();
        int totalArchived = 0;

        for (User user : users) {
            try {
                UserSettings settings = userSettingsRepository.findByUserId(user.getId())
                        .orElse(null);

                if (settings == null || !settings.getAutoArchiveEnabled()) {
                    continue;
                }

                LocalDateTime cutoffDate = LocalDateTime.now()
                        .minusDays(settings.getAutoArchiveDays());

                List<Task> tasksToArchive = taskRepository
                        .findByUserIdAndStatusAndCompletedAtBefore(
                                user.getId(),
                                "done",
                                cutoffDate
                        );

                if (!tasksToArchive.isEmpty()) {
                    tasksToArchive.forEach(task -> {
                        task.setIsArchived(true);
                        task.setArchivedAt(LocalDateTime.now());
                    });

                    taskRepository.saveAll(tasksToArchive);

                    logger.info("Archived {} tasks for user {} ({})",
                            tasksToArchive.size(),
                            user.getId(),
                            user.getEmail());

                    totalArchived += tasksToArchive.size();
                }
            } catch (Exception e) {
                logger.error("Error archiving tasks for user {}: {}",
                        user.getId(), e.getMessage());
            }
        }

        logger.info("Auto-archive job completed. Total tasks archived: {}", totalArchived);
    }
}
