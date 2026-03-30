package com.whatnext.dto.response;

import com.whatnext.model.UserSettings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsResponse {

    private Integer autoArchiveDays;
    private Boolean autoArchiveEnabled;
    private String defaultPriority;
    private String defaultCategory;
    private String defaultDueDate;
    private String theme;
    private String themeColor; // blue, pink, green, yellow, black, red
    private Boolean notificationsEnabled;
    private LocalTime dailySummaryTime;

    public static UserSettingsResponse fromEntity(UserSettings settings) {
        UserSettingsResponse response = new UserSettingsResponse();
        response.setAutoArchiveDays(settings.getAutoArchiveDays());
        response.setAutoArchiveEnabled(settings.getAutoArchiveEnabled());
        response.setDefaultPriority(settings.getDefaultPriority());
        response.setDefaultCategory(settings.getDefaultCategory());
        response.setDefaultDueDate(settings.getDefaultDueDate());
        response.setTheme(settings.getTheme());
        response.setThemeColor(settings.getThemeColor());
        response.setNotificationsEnabled(settings.getNotificationsEnabled());
        response.setDailySummaryTime(settings.getDailySummaryTime());
        return response;
    }
}
