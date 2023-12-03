package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    public UserResponseDto createUser(@RequestBody UserRequestDto user) {
        log.info("Запрошено создание пользователя: {} ", user);
        UserResponseDto createdUser = userService.createUser(user);
        log.info("Запрос на создание пользователя выполнен, пользователь создан: {} ", createdUser);
        return createdUser;
    }

    @GetMapping("/{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        log.info("Запрошен пользователь с id: {} ", id);
        UserResponseDto user = userService.getUser(id);
        log.info("Запрос на пользователя с id {} выполнен: {} ", id, user);
        return user;
    }

    @GetMapping()
    public List<UserResponseDto> getUsers() {
        log.info("Запрошен список Всех пользователей");
        List<UserResponseDto> users = userService.getUsers();
        log.info("Запрос на предоставление списка всех пользователей выполнен");
        return users;
    }

    @PatchMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable Long id, @RequestBody UserRequestDto user) {
        log.info("Запрошено обновление пользователя под id: {} ", id);
        UserResponseDto updatedUser = userService.updateUser(id, user);
        log.info("Запрос выполнен, пользователь обновлен: {} ", updatedUser);
        return updatedUser;
    }
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Запрошено удаление пользователя с id {} ", id);
        userService.deleteUser(id);
        log.info("Запрос на удаление пользователя id {} выполнен", id);
    }
}
