package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    public static final String USER_INVALID_MESSAGE = "id пользователя не должен быть пустым";
    public static final String ITEM_INVALID_MESSAGE = "id вещи не должен быть пустым";

    @PostMapping()
    public ResponseEntity<Object> createItem(@NotNull(message = USER_INVALID_MESSAGE)
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid ItemRequestDto item) {
        log.info("Запрошено создание вещи: {} от пользователя под id {} для запроса {} ", item, userId, item.getRequestId());
        return itemClient.createItem(item, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
                                          @NotNull(message = ITEM_INVALID_MESSAGE)
                                          @PathVariable Long itemId) {
        log.info("Запрошена вещь под id {}", itemId);
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItems(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
                                           @PositiveOrZero(message = "ошибка в параметре 'from")
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive(message = "ошибка в параметре 'size")
                                           @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Запрошен список всех вещей пользователя с id {} c параметрами from {}, size {}", userId, from, size);
        return itemClient.findAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
                                              @RequestParam String text,
                                              @PositiveOrZero(message = "ошибка в параметре 'from")
                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive(message = "ошибка в параметре 'size")
                                              @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Поиск вещей по слову: {} c параметрами from {}, size {}", text, from, size);
        return itemClient.findItemsByRequest(text, userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@NotNull(message = USER_INVALID_MESSAGE)
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @NotNull(message = ITEM_INVALID_MESSAGE)
                                             @PathVariable Long itemId,
                                             @RequestBody ItemRequestDto item) {
        log.info("Запрошено обновление вещи под id: {} от пользователя с id {}", itemId, userId);
        return itemClient.updateItem(item, itemId, userId);
    }


    @DeleteMapping("/{itemId}")
    public void deleteItem(@NotNull(message = USER_INVALID_MESSAGE)
                           @RequestHeader("X-Sharer-User-Id") Long userId,
                           @NotNull(message = ITEM_INVALID_MESSAGE)
                           @PathVariable Long itemId) {
        log.info("Запрошено удаление вещи под id {} пользователя с id {} ", itemId, userId);
        itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@NotNull(message = USER_INVALID_MESSAGE)
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @NotNull(message = ITEM_INVALID_MESSAGE)
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CommentRequestDto comment) {
        log.info("Запрошено создание комментария к вещи с id {} от пользователя под id {}: {} ", itemId, userId, comment);
        return itemClient.createComment(comment, itemId, userId);
    }
}
