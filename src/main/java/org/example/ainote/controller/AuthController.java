package org.example.ainote.controller;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.example.ainote.dto.JwtRequest;
import org.example.ainote.dto.JwtResponse;
import org.example.ainote.dto.UserDTO;
import org.example.ainote.dto.validation.OnCreate;
import org.example.ainote.dto.validation.OnUpdate;
import org.example.ainote.entity.Role;
import org.example.ainote.entity.User;
import org.example.ainote.mapper.UserMapper;
import org.example.ainote.service.AuthService;
import org.example.ainote.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public JwtResponse login(@Validated @RequestBody JwtRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public UserDTO register(@Validated(OnCreate.class) @RequestBody UserDTO userDTO) {
        userDTO.setId(null);
        User user = userMapper.toEntity(userDTO);
        user = userService.create(user);
//        user.setRoles(Set.of(Role.USER));
        return userMapper.toDto(user);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@Validated String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

}
