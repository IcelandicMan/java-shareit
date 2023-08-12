package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Long userId, Long itemId);

    Item getItem(Long itemId);

    List<Item> getItems(Long userId);
}
