package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class BookingResponseDto {

    private Long Id;

    private LocalDateTime start;

    private LocalDateTime end;

    private BookingStatus status;

    private User booker;

    private Item item;

}

