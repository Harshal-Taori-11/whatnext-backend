package com.whatnext.controller;

import com.whatnext.dto.response.MessageResponse;
import com.whatnext.dto.response.TagResponse;
import com.whatnext.security.UserPrincipal;
import com.whatnext.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getUserTags(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<TagResponse> response = tagService.getUserTags(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam String name,
            @RequestParam(required = false) String color) {
        TagResponse response = tagService.createTag(currentUser.getId(), name, color);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTag(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        tagService.deleteTag(currentUser.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Tag deleted successfully"));
    }
}
