package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {

    //Храним <userId <itemId,Item>>
    private final Map<Long, Map<Long, Item>> userItemsMap = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Item createItem(Long userId, Item item) {
        log.info("Создаем вещь от пользователя под id {}: {}", item, userId);
        item.setId(++idCounter);
        item.setOwnerId(userId);
        if (!userItemsMap.containsKey(userId)) {
            userItemsMap.put(userId, new HashMap<>());
        }
        Map<Long, Item> userItems = userItemsMap.get(userId);
        userItems.put(item.getId(), item);
        log.info("Вещь от пользователя под id {} создана: {}", userId, item);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item updatedItem) {
        log.info("Обновление вещи под id {}", itemId);
        Map<Long, Item> userItems = userItemsMap.get(userId);
        if (userItems == null) {
            log.error("Вещь под id {} принадлежит другому пользователю", itemId);
            throw new ItemNotFoundException(String.format("Вещь под id %s принадлежит другому пользователю", itemId));
        }

        final Item item = userItems.get(itemId);
        if (item == null) {
            log.error("Вещь под id {} принадлежит другому пользователю", itemId);
            throw new ItemNotFoundException(String.format("Вещь под id %s принадлежит другому пользователю", itemId));
        }

        final String updatedName = updatedItem.getName();
        final String updatedDescription = updatedItem.getDescription();
        final Boolean updatedStatus = updatedItem.getAvailable();
        if (updatedName != null) {
            item.setName(updatedName);
        }
        if (updatedDescription != null) {
            item.setDescription(updatedDescription);
        }
        if (updatedStatus != null) {
            item.setAvailable(updatedStatus);
        }
        log.info("Вещь под id {} обновлена: {} ", updatedItem.getId(), updatedItem);
        return item;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        log.info("Удаляем вещь c {} от пользователя под id {}", itemId, userId);
        Map<Long, Item> userItems = userItemsMap.get(userId);
        userItems.remove(itemId);
        log.info("Вещь от пользователя под id {} удалена", userId);
    }

    @Override
    public Item getItem(Long itemId) {
        log.info("Получение вещи под id {}", itemId);
        Item item = null;
        for (Long userId : userItemsMap.keySet()) {
            for (Long id : userItemsMap.get(userId).keySet()) {
                if (id.equals(itemId)) {
                    item = userItemsMap.get(userId).get(id);
                }
            }
        }
        if (item == null) {
            log.error("Вещь под id {} не найдена", itemId);
            throw new ItemNotFoundException(String.format("Вещь под id %s не найдена",
                    itemId));
        }
        log.info("Вещь под id {} получена: {}", itemId, item);
        return item;
    }

    @Override
    public List<Item> getItems(Long userId) {
        log.info("Получение Всех вещей пользователя с id {}", userId);
        List<Item> userItemsList = new ArrayList<>();
        if (userId != null) {
            Map<Long, Item> userItems = userItemsMap.get(userId);
            userItemsList.addAll(userItems.values());
        } else {
            for (Map<Long, Item> userItems : userItemsMap.values()) {
                userItemsList.addAll(userItems.values());
            }
        }
        log.info("Вещи пользователя с id {} получены", userId);
        return userItemsList;
    }
}
