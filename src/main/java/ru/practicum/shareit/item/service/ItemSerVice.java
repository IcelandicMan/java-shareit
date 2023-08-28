package ru.practicum.shareit.item.service;


import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemSerVice {

    ItemResponseDto createItem(Long userId, ItemRequestDto item);

    ItemResponseDto getItem(Long userId, Long itemId);

    List<ItemResponseDto> getItems(Long userId);

    ItemResponseDto updateItem(Long userId, Long itemId, ItemRequestDto updatedItem);

    void deleteItem(Long userId, Long itemId);

    List<ItemResponseDto> searchItems(Long userId, String text);

    CommentResponseDto createComment(Long userId, Long itemId, CommentRequestDto comment);
}

