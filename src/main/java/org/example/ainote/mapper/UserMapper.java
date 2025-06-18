package org.example.ainote.mapper;

import org.example.ainote.dto.UserDTO;
import org.example.ainote.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);

}
