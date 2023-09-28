package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto createUser(UserRequestDto user) {
        User createdUser = UserMapper.userRequestDtoToUser(user);
        return UserMapper.userToUserResponseDto(userRepository.save(createdUser));
    }

    @Override
    public UserResponseDto getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь c id %s не найден", id));
        }
        return UserMapper.userToUserResponseDto(user.get());
    }

    @Override
    public List<UserResponseDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::userToUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserRequestDto updatedUser) {
        Optional<User> responseUser = userRepository.findById(userId);
        if (responseUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь c id %s не найден", userId));
        }
        User user = responseUser.get();
        final String email = updatedUser.getEmail();
        final String updatedName = updatedUser.getName();
        user.setId(userId);
        if (email != null) {
            user.setEmail(email);
        }
        if (updatedName != null) {
            user.setName(updatedName);
        }
        return UserMapper.userToUserResponseDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

