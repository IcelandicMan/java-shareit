package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


public interface UserService {
    public UserDto createUser(UserDto user);

    public UserDto getUser(Long id);

    public List<UserDto> getUsers();

    public UserDto updateUser(Long userId, UserDto updatedUser);

    public void deleteUser(Long id);
}
