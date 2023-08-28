package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;



@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item itemRequestDtoToItem(ItemRequestDto item) {
        Item createItem = new Item();
        createItem.setName(item.getName());
        createItem.setDescription(item.getDescription());
        createItem.setAvailable(item.getAvailable());
        return createItem;
    }
}

