package com.whatnext.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterRequest {

    private List<String> statuses; // new, in_progress, done, archived

    private List<String> categories; // work, home, family, etc.

    private List<String> priorities; // high, medium, low, none

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private LocalDate dueDateFrom;

    private LocalDate dueDateTo;

    private String dueDateFilter; // overdue, today, this_week, this_month, no_due_date

    private List<String> tags;

    private String search;

    private String sortBy; // created, updated, due_date, priority, title

    private String sortOrder; // asc, desc

    private Integer page = 0;

    private Integer size = 50;
}
