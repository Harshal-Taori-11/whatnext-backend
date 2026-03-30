package com.whatnext.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserSettingsRequest {

    private Integer autoArchiveDays;

    private Boolean autoArchiveEnabled;

    private String defaultPriority;

    private String defaultCategory;

    private String defaultDueDate;

    private String theme;

    private String themeColor; // blue, pink, green, yellow, black, red

    private Boolean notificationsEnabled;

    private LocalTime dailySummaryTime;
}
