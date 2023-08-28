package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemSerVice;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemSerVice itemService;

    @PostMapping()
    public ItemResponseDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody @Valid ItemRequestDto item) {
        log.info("Запрошено создание вещи: {} от пользователя под id {} ", item, userId);
        ItemResponseDto createdItem = itemService.createItem(userId, item);
        log.info("Запрос на создание вещи от пользователя c id {} выполнен, вещь создана: {} ", userId, createdItem);
        return createdItem;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
                                   @PathVariable Long itemId) {
        log.info("Запрошена вещь под id {}", itemId);
        ItemResponseDto item = itemService.getItem(userId, itemId);
        log.info("Запрос на предоставление вещи с id {} выполнен", item);
        return item;
    }

    @GetMapping()
    public List<ItemResponseDto> getItems(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Запрошен список всех вещей пользователя с id {}", userId);
        List<ItemResponseDto> items = itemService.getItems(userId);
        log.info("Запрос на предоставление списка всех вещей пользователя с id {} выполнен", userId);
        return items;
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
                                             @RequestParam String text) {
        log.info("Поиск вещей по слову: {}", text);
        List<ItemResponseDto> items = itemService.searchItems(userId, text);
        log.info("Запрос вещей по слову {} выполнен", text);
        return items;
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId,
                                      @RequestBody ItemRequestDto item) {
        log.info("Запрошено обновление вещи под id: {} от пользователя с id {}", itemId, userId);
        ItemResponseDto updatedItem = itemService.updateItem(userId, itemId, item);
        log.info("Запрос выполнен, вещь под id: {} обновлена: {}", itemId, updatedItem);
        return updatedItem;
    }


    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Запрошено удаление вещи под id {} пользователя с id {} ", itemId, userId);
        itemService.deleteItem(userId, itemId);
        log.info("Запрос на удаление вещи под id {} пользователя c id {} выполнен", itemId, userId);
    }

    @PostMapping("{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId,
                                            @RequestBody @Valid CommentRequestDto comment) {
        log.info("Запрошено создание комментария к вещи с id {} от пользователя под id {}: {} ", itemId, userId, comment);
        CommentResponseDto createdComment = itemService.createComment(userId, itemId, comment);
        log.info("Запрос на создание комментария к вещи с id {} от пользователя под id {} выполнен. Комментарий создан {}: ",
                itemId, userId, createdComment);
        return createdComment;
    }


}
