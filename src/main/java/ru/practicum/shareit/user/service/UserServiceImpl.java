package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {


    private UserStorage userStorage;
    private ModelMapper userMapper;

    @Override
    public UserDto createUser(@Valid UserDto user) {
        User createdUser = userMapper.map(user, User.class);
        createdUser = userStorage.createUser(createdUser);
        return getUser(createdUser.getId());
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userStorage.getUser(id);
        return userMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userStorage.getUsers();
        return users.stream()
                .map(user -> userMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long userId, UserDto updatedUser) {
        userStorage.getUser(userId);
        User user = userMapper.map(updatedUser, User.class);
        userStorage.updateUser(userId, user);
        return getUser(userId);
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }
}
