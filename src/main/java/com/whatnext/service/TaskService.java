package com.whatnext.service;

import com.whatnext.dto.request.CreateTaskRequest;
import com.whatnext.dto.request.TaskFilterRequest;
import com.whatnext.dto.request.UpdateTaskRequest;
import com.whatnext.dto.response.TaskListResponse;
import com.whatnext.dto.response.TaskResponse;
import com.whatnext.exception.ResourceNotFoundException;
import com.whatnext.model.Tag;
import com.whatnext.model.Task;
import com.whatnext.model.User;
import com.whatnext.repository.TaskRepository;
import com.whatnext.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagService tagService;

    @Transactional
    public TaskResponse createTask(Long userId, CreateTaskRequest request) {
        // Get user to retrieve task code
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Get next task number for this user
        Integer taskNumber = getNextTaskNumber(userId);

        Task task = new Task();
        task.setUserId(userId);
        task.setTaskCode(user.getTaskCode());
        task.setTaskNumber(taskNumber);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : "none");
        task.setCategory(request.getCategory() != null ? request.getCategory() : "other");
        task.setDueDate(request.getDueDate());
        task.setStatus("new");
        task.setPosition(0);

        // Handle tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = tagService.getOrCreateTags(userId, request.getTags());
            task.setTags(tags);
        }

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    private Integer getNextTaskNumber(Long userId) {
        return taskRepository.countByUserId(userId).intValue() + 1;
    }

    @Transactional
    public TaskResponse updateTask(Long userId, Long taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getCategory() != null) task.setCategory(request.getCategory());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getPosition() != null) task.setPosition(request.getPosition());

        // Handle tags
        if (request.getTags() != null) {
            Set<Tag> tags = tagService.getOrCreateTags(userId, request.getTags());
            task.setTags(tags);
        }

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }

        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public void deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }

        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse removeTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }

        task.setStatus("removed");
        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(Long userId, String status) {
        List<Task> tasks = taskRepository.findByUserIdAndStatusAndIsArchivedOrderByPositionAsc(userId, status, false);
        return tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskListResponse getFilteredTasks(Long userId, TaskFilterRequest filter) {
        Specification<Task> spec = createSpecification(userId, filter);

        // Create pageable with sorting
        Sort sort = createSort(filter.getSortBy(), filter.getSortOrder());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Page<Task> taskPage = taskRepository.findAll(spec, pageable);

        TaskListResponse response = new TaskListResponse();
        response.setTasks(taskPage.getContent().stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList()));
        response.setTotalTasks(taskPage.getTotalElements());
        response.setFilteredCount(taskPage.getTotalElements());
        response.setCurrentPage(taskPage.getNumber());
        response.setTotalPages(taskPage.getTotalPages());

        // Get counts
        response.setStatusCounts(getStatusCounts(userId));
        response.setCategoryCounts(getCategoryCounts(userId));

        return response;
    }

    private Specification<Task> createSpecification(Long userId, TaskFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by user
            predicates.add(cb.equal(root.get("userId"), userId));

            // Status filter
            if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
                List<Predicate> statusPredicates = new ArrayList<>();

                for (String status : filter.getStatuses()) {
                    if ("archived".equals(status)) {
                        statusPredicates.add(cb.isTrue(root.get("isArchived")));
                    } else if ("removed".equals(status)) {
                        statusPredicates.add(cb.equal(root.get("status"), "removed"));
                    } else {
                        statusPredicates.add(cb.and(
                            cb.equal(root.get("status"), status),
                            cb.isFalse(root.get("isArchived"))
                        ));
                    }
                }

                predicates.add(cb.or(statusPredicates.toArray(new Predicate[0])));
            } else {
                // By default, exclude removed tasks
                predicates.add(cb.notEqual(root.get("status"), "removed"));
            }

            // Category filter
            if (filter.getCategories() != null && !filter.getCategories().isEmpty()) {
                predicates.add(root.get("category").in(filter.getCategories()));
            }

            // Priority filter
            if (filter.getPriorities() != null && !filter.getPriorities().isEmpty()) {
                predicates.add(root.get("priority").in(filter.getPriorities()));
            }

            // Date range filter
            if (filter.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getDateFrom().atStartOfDay()));
            }
            if (filter.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getDateTo().atTime(23, 59, 59)));
            }

            // Due date filter
            if (filter.getDueDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), filter.getDueDateFrom()));
            }
            if (filter.getDueDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), filter.getDueDateTo()));
            }

            // Due date special filters
            if (filter.getDueDateFilter() != null) {
                LocalDate today = LocalDate.now();
                switch (filter.getDueDateFilter()) {
                    case "overdue":
                        predicates.add(cb.and(
                            cb.isNotNull(root.get("dueDate")),
                            cb.lessThan(root.get("dueDate"), today),
                            cb.notEqual(root.get("status"), "done")
                        ));
                        break;
                    case "today":
                        predicates.add(cb.equal(root.get("dueDate"), today));
                        break;
                    case "this_week":
                        LocalDate weekEnd = today.plusDays(7);
                        predicates.add(cb.between(root.get("dueDate"), today, weekEnd));
                        break;
                    case "this_month":
                        LocalDate monthStart = today.withDayOfMonth(1);
                        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
                        predicates.add(cb.between(root.get("dueDate"), monthStart, monthEnd));
                        break;
                    case "no_due_date":
                        predicates.add(cb.isNull(root.get("dueDate")));
                        break;
                }
            }

            // Search filter
            if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
                String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), searchPattern),
                    cb.like(cb.lower(root.get("description")), searchPattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Sort createSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "updatedAt";
        }

        return Sort.by(direction, sortBy);
    }

    private Map<String, Long> getStatusCounts(Long userId) {
        Map<String, Long> counts = new HashMap<>();
        counts.put("new", taskRepository.countByUserIdAndStatusAndIsArchived(userId, "new", false));
        counts.put("in_progress", taskRepository.countByUserIdAndStatusAndIsArchived(userId, "in_progress", false));
        counts.put("done", taskRepository.countByUserIdAndStatusAndIsArchived(userId, "done", false));
        counts.put("removed", taskRepository.countByUserIdAndStatus(userId, "removed"));

        // Count archived
        Specification<Task> archivedSpec = (root, query, cb) ->
            cb.and(cb.equal(root.get("userId"), userId), cb.isTrue(root.get("isArchived")));
        counts.put("archived", taskRepository.count(archivedSpec));

        return counts;
    }

    private Map<String, Long> getCategoryCounts(Long userId) {
        List<Object[]> results = taskRepository.countByCategory(userId);
        Map<String, Long> counts = new HashMap<>();

        for (Object[] result : results) {
            String category = (String) result[0];
            Long count = (Long) result[1];
            counts.put(category, count);
        }

        return counts;
    }

    @Transactional
    public TaskResponse archiveTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }

        task.setIsArchived(true);
        task.setArchivedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional
    public TaskResponse restoreTask(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }

        task.setIsArchived(false);
        task.setArchivedAt(null);

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }
}
