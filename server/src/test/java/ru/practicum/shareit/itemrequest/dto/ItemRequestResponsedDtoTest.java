package ru.practicum.shareit.itemrequest.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestResponsedDtoTest {

    @Autowired
    private JacksonTester<ItemRequestResponsedDto> json;

    @Test
    public void itemRequestResponsedDtoTest() throws Exception {
        LocalDateTime created = LocalDateTime.of(2023, 12, 1, 9, 0);

        ItemRequestResponsedDto responsedDto = new ItemRequestResponsedDto();
        responsedDto.setId(1L);
        responsedDto.setCreated(created);
        responsedDto.setDescription("Запрос");

        JsonContent<ItemRequestResponsedDto> result = json.write(responsedDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Запрос");
    }
}
