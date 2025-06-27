package org.example.ainote.service;

import lombok.RequiredArgsConstructor;
import org.example.ainote.entity.Note;
import org.example.ainote.entity.Role;
import org.example.ainote.exception.EntityNotFoundException;
import org.example.ainote.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.ainote.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found!"));
    }

    @Transactional
    public User update(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User create(User user) {
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new IllegalArgumentException("Username already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userRepository.save(user);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean isNoteOwner(Long noteId, Long userId) {
        return userRepository.isNoteOwner(noteId, userId);
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }



}
