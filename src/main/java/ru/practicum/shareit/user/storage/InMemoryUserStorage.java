package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.EmailIsUsedException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Set<String> emailUniqSet = new HashSet<>();
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public User createUser(User user) {
        log.info("Создание пользователя: {}", user);
        final String email = user.getEmail();
        if (emailUniqSet.contains(email)) {
            throw new EmailIsUsedException(String.format("Пользователь c email %s уже существует", user.getEmail()));
        }
        user.setId(++idCounter);
        user.setRegistryDate(LocalDate.now());
        emailUniqSet.add(email);
        users.put(user.getId(), user);
        log.info("Пользователь под id {} создан: {}", user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        log.info("Обновление пользователя под id {}", updatedUser.getId());
        final Long userId = updatedUser.getId();
        final String email = updatedUser.getEmail();
        final String updatedName = updatedUser.getName();
        User user = getUser(userId);
        if (email != null && !email.equalsIgnoreCase(user.getEmail())) {
            if (emailUniqSet.contains(email)) {
                throw new EmailIsUsedException(String.format("Пользователь c email %s уже существует",
                        updatedUser.getEmail()));
            }
            emailUniqSet.remove(user.getEmail());
            emailUniqSet.add(email);
            user.setEmail(email);
        }
        if (updatedName != null) {
            user.setName(updatedName);
        }
        log.info("Пользователь под id {} обновлен: {} ", updatedUser.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id {}", id);
        emailUniqSet.remove(getUser(id).getEmail());
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
}