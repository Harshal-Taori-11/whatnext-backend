package com.whatnext.repository;

import com.whatnext.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findByUserIdAndIsArchivedOrderByPositionAsc(Long userId, Boolean isArchived);

    List<Task> findByUserIdAndStatusAndIsArchivedOrderByPositionAsc(Long userId, String status, Boolean isArchived);

    Page<Task> findByUserId(Long userId, Pageable pageable);

    List<Task> findByUserIdAndStatusAndCompletedAtBefore(Long userId, String status, LocalDateTime completedAt);

    Long countByUserId(Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.status = :status AND t.isArchived = :isArchived")
    Long countByUserIdAndStatusAndIsArchived(@Param("userId") Long userId, @Param("status") String status, @Param("isArchived") Boolean isArchived);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Query("SELECT t.category, COUNT(t) FROM Task t WHERE t.userId = :userId GROUP BY t.category")
    List<Object[]> countByCategory(@Param("userId") Long userId);
}
