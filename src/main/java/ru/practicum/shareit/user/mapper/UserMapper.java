package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User userRequestDtoToUser(UserRequestDto user) {
        User createdUser = new User();
        createdUser.setName(user.getName());
        createdUser.setEmail(user.getEmail());
        return createdUser;
    }

    public static UserResponseDto userToUserResponseDto(User user) {
        UserResponseDto createdUser = new UserResponseDto();
        createdUser.setId(user.getId());
        createdUser.setName(user.getName());
        createdUser.setEmail(user.getEmail());
        return createdUser;
    }

}

