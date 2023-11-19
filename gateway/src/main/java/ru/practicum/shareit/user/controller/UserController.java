package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    public static final String USER_INVALID_MESSAGE = "id пользователя не должен быть пустым";

    @PostMapping()
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequestDto user) {
        log.info("Запрошено создание пользователя: {} ", user);
        return userClient.createUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@NotNull(message = USER_INVALID_MESSAGE)
                                          @PathVariable Long id) {
        log.info("Запрошен пользователь с id: {} ", id);
        return userClient.findUserById(id);
    }

    @GetMapping()
    public ResponseEntity<Object> getUsers() {
        log.info("Запрошен список Всех пользователей");
        return userClient.findAllUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@NotNull(message = USER_INVALID_MESSAGE)
                                             @PathVariable Long id,
                                             @RequestBody UserRequestDto user) {
        log.info("Запрошено обновление пользователя под id: {} ", id);
        return userClient.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@NotNull(message = USER_INVALID_MESSAGE)
                           @PathVariable Long id) {
        log.info("Запрошено удаление пользователя с id {} ", id);
        userClient.deleteUserById(id);
    }
}
