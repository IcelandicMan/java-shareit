package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;


public interface UserService {
    public UserResponseDto createUser(UserRequestDto user);

    public UserResponseDto getUser(Long id);

    public List<UserResponseDto> getUsers();

    public UserResponseDto updateUser(Long userId, UserRequestDto updatedUser);

    public void deleteUser(Long id);
}
