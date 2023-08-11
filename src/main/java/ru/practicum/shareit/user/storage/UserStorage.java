package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User updateUser(Long userId, User updatedUser);

    void deleteUser(Long id);

    User getUser(Long id);

    List<User> getUsers();
}