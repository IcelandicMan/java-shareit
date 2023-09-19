package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserResponseDtoTest {

    @Autowired
    private JacksonTester<UserResponseDto> json;

    @Test
    public void UserResponseDtoTest() throws Exception {
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(1L);
        userDto.setName("Test");
        userDto.setEmail("test@mail.com");

        JsonContent<UserResponseDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@mail.com");
    }
}
