package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemSerVice {

    ItemDto createItem(Long userId, ItemDto item);

    ItemDto getItem(Long itemId);

    List<ItemDto> getItems(Long userId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto updatedItem);

    void deleteItem(Long userId, Long itemId);

    List<ItemDto> searchItems(String text);
}

