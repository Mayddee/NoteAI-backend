package org.example.ainote.service;

import lombok.RequiredArgsConstructor;
import org.example.ainote.dto.JwtRequest;
import org.example.ainote.dto.JwtResponse;
import org.example.ainote.entity.User;
import org.example.ainote.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private Authentication authenticate(Authentication authentication) {
        Authentication authenticated = authenticationManager.authenticate(authentication);
        return authenticated;
    }

    public JwtResponse login(JwtRequest jwtRequest) {
        JwtResponse jwtResponse = new JwtResponse();
        authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        User user = userService.getByUsername(jwtRequest.getUsername());
        jwtResponse.setId(user.getId());
        jwtResponse.setName(user.getName());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(jwtTokenProvider.createAccessToken(user));
        jwtResponse.setRefreshToken(jwtTokenProvider.createRefreshToken(user));
        return jwtResponse;

    }

    public JwtResponse refreshToken(String refreshToken) {
        return jwtTokenProvider.refresh(refreshToken);
    }


}
