package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemSerVice;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemSerVice itemService;

    @PostMapping()
    public ItemResponseDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody ItemRequestDto item) {
        log.info("Запрошено создание вещи: {} от пользователя под id {} для запроса {} ", item, userId, item.getRequestId());
        ItemResponseDto createdItem = itemService.createItem(userId, item);
        log.info("Запрос на создание вещи от пользователя c id {} выполнен, вещь создана для запроса {}: {} ", userId,
                item.getRequestId(), createdItem);
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
    public List<ItemResponseDto> getItems(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
                                          @RequestParam(name = "from") Integer from,
                                          @RequestParam(name = "size") Integer size) {
        log.info("Запрошен список всех вещей пользователя с id {} c параметрами from {}, size {}", userId, from, size);
        List<ItemResponseDto> items = itemService.getItems(userId, from, size);
        log.info("Запрос на предоставление списка всех вещей пользователя с id {} параметрами from {}, size {} -  выполнен",
                userId, from, size);
        return items;
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
                                             @RequestParam String text,
                                             @RequestParam(name = "from") Integer from,
                                             @RequestParam(name = "size") Integer size) {
        log.info("Поиск вещей по слову: {} c параметрами from {}, size {}", text, from, size);
        List<ItemResponseDto> items = itemService.searchItems(userId, text, from, size);
        log.info("Запрос вещей по слову {} c параметрами from {}, size {} - выполнен", text, from, size);
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
                                            @RequestBody CommentRequestDto comment) {
        log.info("Запрошено создание комментария к вещи с id {} от пользователя под id {}: {} ", itemId, userId, comment);
        CommentResponseDto createdComment = itemService.createComment(userId, itemId, comment);
        log.info("Запрос на создание комментария к вещи с id {} от пользователя под id {} выполнен. Комментарий создан {}: ",
                itemId, userId, createdComment);
        return createdComment;
    }


}
