package org.example.ainote.service;

import lombok.RequiredArgsConstructor;
import org.example.ainote.entity.Note;
import org.example.ainote.entity.User;
import org.example.ainote.exception.EntityNotFoundException;
import org.example.ainote.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Note getById(Long id) {
        return noteRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Note not found."));
    }

    @Transactional
    public Note createNoteForUser(Long userId, String title, String content) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        noteRepository.save(note);
        User user = userService.getById(userId);
        user.getNotes().add(note);
        userService.update(user);
        return note;
    }

    @Transactional
    public List<Note> getAllByUserId(Long userId){
        return noteRepository.findAllByUserId(userId).orElseThrow(()->new EntityNotFoundException("Notes not found."));
    }

    @Transactional(readOnly = true)
    public Note getNote(Long userId, Long noteId){
        if(!userService.isNoteOwner(noteId, userId)){
            throw new EntityNotFoundException("Note not found!");
        }
        return getById(noteId);
    }
    
}
