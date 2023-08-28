package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;


import java.util.*;

@Slf4j
@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Item createItem(Item item) {
        log.info("Создаем вещь от пользователя под id {}: {}", item, item.getOwnerId());
        Long ownerId = item.getOwnerId();
        item.setId(++idCounter);
        items.put(item.getId(), item);
        userItemIndex.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(item);
        log.info("Вещь от пользователя под id {} создана: {}", ownerId, item);
        return item;
    }

    @Override
    public Item updateItem(Item updatedItem) {
        log.info("Обновление вещи под id {}", updatedItem.getId());
        final Long itemId = updatedItem.getId();
        final Long userId = updatedItem.getOwnerId();

        List<Item> userItems = userItemIndex.get(userId);
        if (userItems == null) {
            log.error("Вещь под id {} принадлежит другому пользователю", updatedItem.getId());
            throw new ItemNotFoundException(String.format("Вещь под id %s принадлежит другому пользователю", itemId));
        }

        Item itemToUpdate = items.get(itemId);
        if (itemToUpdate == null || !userItems.contains(itemToUpdate)) {
            log.error("Вещь под id {} принадлежит другому пользователю", itemId);
            throw new ItemNotFoundException(String.format("Вещь под id %s принадлежит другому пользователю", itemId));
        }

        if (updatedItem.getName() != null) {
            itemToUpdate.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            itemToUpdate.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            itemToUpdate.setAvailable(updatedItem.getAvailable());
        }

        for (int i = 0; i < userItems.size(); i++) {
            if (userItems.get(i).getId().equals(itemId)) {
                userItems.set(i, itemToUpdate);
                break;
            }
        }

        log.info("Вещь под id {} обновлена: {} ", itemId, updatedItem);
        return itemToUpdate;
    }


    @Override
    public Item getItem(Long itemId) {
        log.info("Получение вещи под id {}", itemId);
        Item item = null;
        item = items.get(itemId);
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
        List<Item> allItems = new ArrayList<>();
        if (userId != null) {
            return userItemIndex.get(userId);
        }
        for (List<Item> userItems : userItemIndex.values()) {
            allItems.addAll(userItems);

        }
        log.info("Вещи пользователя с id {} получены", userId);
        return allItems;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        log.info("Удаляем вещь c {} от пользователя под id {}", itemId, userId);
        List<Item> userItems = userItemIndex.get(userId);
        if (userItems != null) {
            userItems.removeIf(item -> item.getId().equals(itemId));
            items.remove(itemId);
            log.info("Вещь от пользователя под id {} удалена", userId);
        }
    }
}
