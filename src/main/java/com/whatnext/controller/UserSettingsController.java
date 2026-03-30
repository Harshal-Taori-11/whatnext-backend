package com.whatnext.controller;

import com.whatnext.dto.request.UpdateUserSettingsRequest;
import com.whatnext.dto.response.UserSettingsResponse;
import com.whatnext.security.UserPrincipal;
import com.whatnext.service.UserSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class UserSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    @GetMapping
    public ResponseEntity<UserSettingsResponse> getUserSettings(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        UserSettingsResponse response = userSettingsService.getUserSettings(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UserSettingsResponse> updateUserSettings(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody UpdateUserSettingsRequest request) {
        UserSettingsResponse response = userSettingsService.updateUserSettings(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }
}
