package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;

@Data
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentResponseDto> comments;
}
