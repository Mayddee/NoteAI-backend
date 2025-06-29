package org.example.ainote.controller;

import lombok.RequiredArgsConstructor;
import org.example.ainote.dto.NoteDTO;
import org.example.ainote.dto.validation.OnUpdate;
import org.example.ainote.entity.Note;
import org.example.ainote.mapper.NoteMapper;
import org.example.ainote.security.JwtEntity;
import org.example.ainote.security.JwtTokenProvider;
import org.example.ainote.service.NoteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
@Validated
public class NoteController {
    private final NoteService noteService;
    private final NoteMapper noteMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public List<NoteDTO> getTasksForCurrentUser(@AuthenticationPrincipal JwtEntity user) {
        return noteService.getAllByUserId(user.getId()).stream().map(noteMapper::toDto).collect(Collectors.toList());

    }

    @GetMapping("/{id}")
    public NoteDTO getNoteById(@AuthenticationPrincipal JwtEntity user, @PathVariable Long id) {
        return noteMapper.toDto(noteService.getNote(user.getId(), id));
    }

    @DeleteMapping("/{id}")
    public void deleteNoteById(@AuthenticationPrincipal JwtEntity user, @PathVariable Long id) {
        noteService.deleteNote(user.getId(), id);
    }

    @PutMapping
    public NoteDTO updateNote(@AuthenticationPrincipal JwtEntity user, @Validated(OnUpdate.class) @RequestBody NoteDTO note) {
        Note noteEntity = noteMapper.toEntity(note);
        Note updatedNote = noteService.updateNote(user.getId(), noteEntity);
        return noteMapper.toDto(updatedNote);

    }



}
