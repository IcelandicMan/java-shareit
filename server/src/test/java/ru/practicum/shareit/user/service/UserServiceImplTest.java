package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private UserRequestDto userRequestDto;
    private UserRequestDto userRequestDto2;

    private User user;
    private User user2;
    private UserResponseDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Arthur");
        user.setEmail("arthur@gmail.com");

        user2 = new User();
        user2.setId(2L);
        user2.setName("Jonsi");
        user2.setEmail("jonsi@gmail.com");

        userRequestDto = new UserRequestDto();
        userRequestDto.setName("Arthur");
        userRequestDto.setEmail("arthur@gmail.com");

        userRequestDto2 = new UserRequestDto();
        userRequestDto2.setName("Jonsi");
        userRequestDto2.setEmail("jonsi@gmail.com");
    }


    @Test
    void createUserTest() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        userDto = userService.createUser(userRequestDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserAndThrowExceptionIfEmptyTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        userDto = userService.getUser(1L);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());

        verify(userRepository, times(1)).findById(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(1L);
        });

        verify(userRepository, times(2)).findById(1L);
    }

    @Test
    void getUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserResponseDto> users = userService.getUsers();
        assertEquals(2, users.size());
        assertEquals(users.get(0), UserMapper.userToUserResponseDto(user));
        assertEquals(users.get(1), UserMapper.userToUserResponseDto(user2));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userRequestDto.setName("Новое имя");
        userRequestDto.setEmail("updated@mail.com");

        userDto = userService.updateUser(1L, userRequestDto);

        assertEquals(userRequestDto.getName(), userDto.getName());
        assertEquals("Новое имя", userDto.getName());
        assertEquals(userRequestDto.getEmail(), userDto.getEmail());
        assertEquals(1, userDto.getId());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserIfNotExistTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user.getId(), userRequestDto));
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}