package com.whatnext.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskListResponse {

    private List<TaskResponse> tasks;
    private Long totalTasks;
    private Long filteredCount;
    private Map<String, Long> statusCounts;
    private Map<String, Long> categoryCounts;
    private Integer currentPage;
    private Integer totalPages;
}
