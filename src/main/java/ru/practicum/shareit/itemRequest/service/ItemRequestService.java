package ru.practicum.shareit.itemRequest.service;

import ru.practicum.shareit.itemRequest.dto.ItemRequestRequestedDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponsedDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponsedDto createItemRequest(Long requestorId, ItemRequestRequestedDto itemRequest);

    ItemRequestResponsedDto getItemRequest(Long userId, Long itemRequestId);

    List<ItemRequestResponsedDto> getAllItemRequestByOwner(Long userId, Integer from, Integer size);

    List<ItemRequestResponsedDto> getAllItemRequest(Long userId, Integer from, Integer size);

}
