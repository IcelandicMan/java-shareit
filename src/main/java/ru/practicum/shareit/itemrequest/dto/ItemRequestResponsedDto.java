package ru.practicum.shareit.itemrequest.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class ItemRequestResponsedDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForItemRequestDto> items;
}
