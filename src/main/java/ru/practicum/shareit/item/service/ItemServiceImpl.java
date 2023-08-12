package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemSerVice {


    private ItemStorage itemStorage;
    private UserStorage userStorage;
    private ModelMapper itemMapper;

    @Override
    public ItemDto createItem(Long userId, @Valid ItemDto item) {
        userStorage.getUser(userId);
        Item createdItem = itemMapper.map(item, Item.class);
        createdItem.setOwnerId(userId);
        createdItem = itemStorage.createItem(createdItem);
        return getItem(createdItem.getId());
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemStorage.getItem(itemId);
        return itemMapper.map(item, ItemDto.class);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        if (userId != null) {
            userStorage.getUser(userId);
        }
        List<Item> items = itemStorage.getItems(userId);
        return items.stream()
                .map(item -> itemMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto updatedItem) {
        userStorage.getUser(userId); // сразу прекращаем работу, если пользователя не существует
        Item item = itemMapper.map(updatedItem, Item.class);
        item.setOwnerId(userId);
        item.setId(itemId);
        itemStorage.updateItem(item);
        return getItem(itemId);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        userStorage.getUser(userId);
        itemStorage.deleteItem(userId, itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> foundItems = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemStorage.getItems(null);
        for (Item item : items) {
            if ((item.getDescription().toLowerCase().contains(text.toLowerCase())
                    || item.getName().toLowerCase().contains(text.toLowerCase()))
                    && item.getAvailable()) {

                foundItems.add(item);
            }
        }
        return foundItems.stream()
                .map(item -> itemMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }
}

