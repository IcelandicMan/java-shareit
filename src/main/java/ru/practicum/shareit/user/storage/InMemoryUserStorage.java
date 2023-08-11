package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.EmailIsUsedException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public User createUser(User user) {
        log.info("Создание пользователя: {}", user);
        isEmailUsed(user);
        user.setId(++idCounter);
        user.setRegistryDate(LocalDate.now());
        users.put(user.getId(), user);
        log.info("Пользователь под id {} создан: {}", user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long userId, User updatedUser) {
        log.info("Обновление пользователя под id {}", userId);
        final User user = getUser(userId);
        final String updatedName = updatedUser.getName();
        final String email = updatedUser.getEmail();
        if (updatedName != null) {
            user.setName(updatedName);
        }
        if (email != null && email.equalsIgnoreCase(user.getEmail())) {
            // Ничего не делаем, потому что адрес электронной почты остался неизменным
        } else if (email != null) {
            isEmailUsed(updatedUser);
            user.setEmail(email);
        }
        log.info("Пользователь под id {} обновлен: {} ", updatedUser.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id {}", id);
        users.remove(id);
        log.info("Пользователь с id {} удален", id);
    }

    @Override
    public User getUser(Long id) {
        log.info("Получение пользователя с id {}", id);
        User user = users.get(id);
        if (user == null) {
            log.error("Пользователь под id {} не найден", id);
            throw new UserNotFoundException(String.format("Пользователь c id %s не найден", id));
        }
        log.info("Пользователь с id {} получен: {}", id, user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        List<User> usersList = new ArrayList<>(users.values());
        log.info("Список всех пользователей получен");
        return usersList;
    }

    private void isEmailUsed(User user) {
        for (User u : users.values()) {
            if (u.equals(user)) {
                continue;
            }
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                throw new EmailIsUsedException(String.format("Пользователь c email %s уже существует", user.getEmail()));
            }
        }
    }
}