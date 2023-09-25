package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;


    private UserRequestDto userRequestDto;
    private UserRequestDto userRequestDto2;

    private UserResponseDto userResponseDto;
    private UserResponseDto userResponseDto2;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("Arthur");
        userRequestDto.setEmail("arthur@gmail.com");

        userRequestDto2 = new UserRequestDto();
        userRequestDto2.setName("Jonsi");
        userRequestDto2.setEmail("jonsi@gmail.com");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName(userRequestDto.getName());
        userResponseDto.setEmail(userRequestDto.getEmail());

        userResponseDto2 = new UserResponseDto();
        userResponseDto2.setId(2L);
        userResponseDto2.setName(userRequestDto2.getName());
        userResponseDto2.setEmail(userRequestDto2.getEmail());

    }

    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userResponseDto.getEmail()), String.class));

        verify(userService, times(1)).createUser(userRequestDto);
    }

    @Test
    void createUserWithNotValidTest() throws Exception {
        when(userService.createUser(any(UserRequestDto.class))).thenThrow(new RuntimeException());
        userRequestDto.setName(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(userRequestDto);
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUser(1L)).thenReturn(userResponseDto);

        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Arthur")))
                .andExpect(jsonPath("$.email", is("arthur@gmail.com")));
    }

    @Test
    void getUsersTest() throws Exception {
        List<UserResponseDto> userList = new ArrayList<>();
        userList.add(userResponseDto);
        userList.add(userResponseDto2);

        when(userService.getUsers()).thenReturn(userList);

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Проверяем, что возвращается список с двумя пользователями
                .andExpect(jsonPath("$[0].id", is(1))) // Проверяем первого пользователя
                .andExpect(jsonPath("$[0].name", is("Arthur")))
                .andExpect(jsonPath("$[0].email", is("arthur@gmail.com")))
                .andExpect(jsonPath("$[1].id", is(2))) // Проверяем второго пользователя
                .andExpect(jsonPath("$[1].name", is("Jonsi")))
                .andExpect(jsonPath("$[1].email", is("jonsi@gmail.com")));
    }

    @Test
    void updateUserTest() throws Exception {

        UserResponseDto expectedUserResponse = new UserResponseDto();
        expectedUserResponse.setId(6L);
        expectedUserResponse.setName(userRequestDto.getName());
        expectedUserResponse.setEmail(userRequestDto.getEmail());

        when(userService.updateUser(eq(6L), eq(userRequestDto))).thenAnswer(invocation -> expectedUserResponse);

        MvcResult result = mvc.perform(patch("/users/{userId}", 6)
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();


        String responseJson = result.getResponse().getContentAsString();
        UserResponseDto actualUserResponse = mapper.readValue(responseJson, UserResponseDto.class);

        assertEquals(expectedUserResponse.getId(), actualUserResponse.getId());
        assertEquals(expectedUserResponse.getName(), actualUserResponse.getName());
        assertEquals(expectedUserResponse.getEmail(), actualUserResponse.getEmail());
    }
}