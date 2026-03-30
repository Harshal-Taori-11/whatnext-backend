package com.whatnext.service;

import com.whatnext.dto.request.UpdateUserSettingsRequest;
import com.whatnext.dto.response.UserSettingsResponse;
import com.whatnext.exception.ResourceNotFoundException;
import com.whatnext.model.UserSettings;
import com.whatnext.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Transactional(readOnly = true)
    public UserSettingsResponse getUserSettings(Long userId) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSettings", "userId", userId));

        return UserSettingsResponse.fromEntity(settings);
    }

    @Transactional
    public UserSettingsResponse updateUserSettings(Long userId, UpdateUserSettingsRequest request) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSettings", "userId", userId));

        if (request.getAutoArchiveDays() != null) {
            settings.setAutoArchiveDays(request.getAutoArchiveDays());
        }
        if (request.getAutoArchiveEnabled() != null) {
            settings.setAutoArchiveEnabled(request.getAutoArchiveEnabled());
        }
        if (request.getDefaultPriority() != null) {
            settings.setDefaultPriority(request.getDefaultPriority());
        }
        if (request.getDefaultCategory() != null) {
            settings.setDefaultCategory(request.getDefaultCategory());
        }
        if (request.getDefaultDueDate() != null) {
            settings.setDefaultDueDate(request.getDefaultDueDate());
        }
        if (request.getTheme() != null) {
            settings.setTheme(request.getTheme());
        }
        if (request.getThemeColor() != null) {
            settings.setThemeColor(request.getThemeColor());
        }
        if (request.getNotificationsEnabled() != null) {
            settings.setNotificationsEnabled(request.getNotificationsEnabled());
        }
        if (request.getDailySummaryTime() != null) {
            settings.setDailySummaryTime(request.getDailySummaryTime());
        }

        UserSettings savedSettings = userSettingsRepository.save(settings);
        return UserSettingsResponse.fromEntity(savedSettings);
    }
}
