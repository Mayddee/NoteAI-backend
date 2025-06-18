package org.example.ainote.security;

import org.example.ainote.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtEntityFactory {

    public static JwtEntity generateUserDetails(User user) {
        return new JwtEntity(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                user.getRoles()
        );

    }
}
