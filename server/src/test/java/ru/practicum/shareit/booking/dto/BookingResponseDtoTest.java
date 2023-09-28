package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    public void bookingResponseDtoTest() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 12, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 10, 14, 0);

        User user = new User();
        user.setId(6L);
        user.setName("Arthur");
        user.setEmail("arthur@gmail.com");


        Item item = new Item();
        item.setId(3L);
        item.setName("Укулеле");
        item.setAvailable(true);
        item.setDescription("Нейлоновые струны");


        BookingResponseDto bookingDto = new BookingResponseDto();
        bookingDto.setItem(item);
        bookingDto.setBooker(user);
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(BookingStatus.APPROVED);

        JsonContent<BookingResponseDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(6);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Arthur");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("arthur@gmail.com");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Укулеле");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Нейлоновые струны");
    }
}
