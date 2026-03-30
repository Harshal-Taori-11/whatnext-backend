package com.whatnext.controller;

import com.whatnext.dto.request.CreateNoteRequest;
import com.whatnext.dto.request.UpdateNoteRequest;
import com.whatnext.dto.response.MessageResponse;
import com.whatnext.dto.response.NoteResponse;
import com.whatnext.security.UserPrincipal;
import com.whatnext.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateNoteRequest request) {
        NoteResponse response = noteService.createNote(currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getUserNotes(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<NoteResponse> response = noteService.getUserNotes(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteResponse>> searchNotes(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam String q) {
        List<NoteResponse> response = noteService.searchNotes(currentUser.getId(), q);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNote(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        NoteResponse response = noteService.getNote(currentUser.getId(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UpdateNoteRequest request) {
        NoteResponse response = noteService.updateNote(currentUser.getId(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteNote(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        noteService.deleteNote(currentUser.getId(), id);
        return ResponseEntity.ok(new MessageResponse("Note deleted successfully"));
    }
}
