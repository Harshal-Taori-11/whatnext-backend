package com.whatnext.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    private String description;

    private String status; // new, in_progress, done

    private String priority; // none, low, medium, high

    private String category; // work, home, family, routine, health, finance, personal, other

    private LocalDate dueDate;

    private Integer position;

    private Set<String> tags;
}
