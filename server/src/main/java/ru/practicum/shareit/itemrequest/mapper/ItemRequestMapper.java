package ru.practicum.shareit.itemrequest.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestedDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestResponsedDto;
import ru.practicum.shareit.itemrequest.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest itemRequestResponsedDtoToItemRequest(ItemRequestRequestedDto itemRequest) {
        ItemRequest createdItemRequest = new ItemRequest();
        createdItemRequest.setDescription(itemRequest.getDescription());
        return createdItemRequest;
    }

    public static ItemRequestResponsedDto itemRequestToitemRequestResponsedDto(ItemRequest itemRequest) {
        ItemRequestResponsedDto itemRequestResponsedDto = new ItemRequestResponsedDto();
        itemRequestResponsedDto.setId(itemRequest.getId());
        itemRequestResponsedDto.setDescription(itemRequest.getDescription());
        itemRequestResponsedDto.setCreated(itemRequest.getCreated());
        List<Item> itemList = itemRequest.getItems();
        List<ItemForItemRequestDto> itemForItemRequestDtoList = itemList.stream()
                .map(ItemMapper::itemToItemForItemRequestDto).collect(Collectors.toList());
        itemRequestResponsedDto.setItems(itemForItemRequestDtoList);
        return itemRequestResponsedDto;
    }

}
