package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemSerVice;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemSerVice itemService;

    @Autowired
    public ItemController(ItemSerVice itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemDto item) {
        log.info("Запрошено создание вещи: {} от пользователя под id {} ", item, userId);
        ItemDto createdItem = itemService.createItem(userId, item);
        log.info("Запрос на создание вещи от пользователя c id {} выполнен, вещь создана: {} ", userId, createdItem);
        return createdItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Запрошена вещь под id {}", itemId);
        ItemDto item = itemService.getItem(itemId);
        log.info("Запрос на предоставление вещи с id {} выполнен", item);
        return item;
    }

    @GetMapping()
    public List<ItemDto> getItems(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Запрошен список всех вещей пользователя с id {}", userId);
        List<ItemDto> items = itemService.getItems(userId);
        log.info("Запрос на предоставление списка всех вещей пользователя с id {} выполнен", userId);
        return items;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Поиск вещей по слову: {}", text);
        List<ItemDto> items = itemService.searchItems(text);
        log.info("Запрос вещей по слову {} выполнен", text);
        return items;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto item) {
        log.info("Запрошено обновление вещи под id: {} от пользователя с id {}", itemId, userId);
        ItemDto updatedItem = itemService.updateItem(userId, itemId, item);
        log.info("Запрос выполнен, вещь под id: {} обновлена: {}", itemId, updatedItem);
        return updatedItem;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Запрошено удаление вещи под id {} пользователя с id {} ", itemId, userId);
        itemService.deleteItem(userId, itemId);
        log.info("Запрос на удаление вещи под id {} пользователя c id {} выполнен", itemId, userId);
    }
}
