package com.whatnext.service;

import com.whatnext.dto.request.CreateNoteRequest;
import com.whatnext.dto.request.UpdateNoteRequest;
import com.whatnext.dto.response.NoteResponse;
import com.whatnext.exception.ResourceNotFoundException;
import com.whatnext.model.Note;
import com.whatnext.model.Tag;
import com.whatnext.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private TagService tagService;

    @Transactional
    public NoteResponse createNote(Long userId, CreateNoteRequest request) {
        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        // Handle tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = tagService.getOrCreateTags(userId, request.getTags());
            note.setTags(tags);
        }

        Note savedNote = noteRepository.save(note);
        return NoteResponse.fromEntity(savedNote);
    }

    @Transactional
    public NoteResponse updateNote(Long userId, Long noteId, UpdateNoteRequest request) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        if (!note.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Note", "id", noteId);
        }

        if (request.getTitle() != null) note.setTitle(request.getTitle());
        if (request.getContent() != null) note.setContent(request.getContent());

        // Handle tags
        if (request.getTags() != null) {
            Set<Tag> tags = tagService.getOrCreateTags(userId, request.getTags());
            note.setTags(tags);
        }

        Note savedNote = noteRepository.save(note);
        return NoteResponse.fromEntity(savedNote);
    }

    @Transactional(readOnly = true)
    public NoteResponse getNote(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        if (!note.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Note", "id", noteId);
        }

        return NoteResponse.fromEntity(note);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> getUserNotes(Long userId) {
        return noteRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(NoteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> searchNotes(Long userId, String search) {
        return noteRepository.searchNotes(userId, search).stream()
                .map(NoteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteNote(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        if (!note.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Note", "id", noteId);
        }

        noteRepository.delete(note);
    }
}
