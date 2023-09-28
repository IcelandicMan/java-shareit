package ru.practicum.shareit.itemrequest.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.client.ItemRequestClient;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestedDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {


    private final ItemRequestClient itemRequestClient;

    public static final String USER_INVALID_MESSAGE = "id пользователя не должен быть пустым";
    public static final String REQUEST_INVALID_MESSAGE = "id запроса не должен быть пустым";

    @PostMapping()
    public ResponseEntity<Object> createItemRequest(@NotNull(message = USER_INVALID_MESSAGE)
                                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody @Valid ItemRequestRequestedDto itemRequest) {
        log.info("Запрошено создание запроса вещи: {} от пользователя под id {} ", itemRequest, userId);
        return itemRequestClient.createRequest(itemRequest, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@NotNull(message = USER_INVALID_MESSAGE)
                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @NotNull(message = REQUEST_INVALID_MESSAGE)
                                                 @PathVariable Long requestId) {
        log.info("Запрошен Запрос вещи под id {} от пользователя с id {}", requestId, userId);
        return itemRequestClient.findById(requestId, userId);
    }


    @GetMapping()
    public ResponseEntity<Object> getAllItemRequestByUser(@NotNull(message = USER_INVALID_MESSAGE)
                                                          @RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @PositiveOrZero(message = "ошибка в параметре 'from")
                                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive(message = "ошибка в параметре 'size")
                                                          @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Запрошены все запросы на вещи от пользователя с id {} с параметрами страницы from {} size {} ", userId, from, size);
        return itemRequestClient.findAllByUserId(from, size, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@NotNull(message = USER_INVALID_MESSAGE)
                                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PositiveOrZero(message = "ошибка в параметре 'from")
                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive(message = "ошибка в параметре 'size")
                                                    @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Запрошены все запросы на вещи от пользователя с id {} с параметрами страницы from {} size {} ", userId, from, size);
        return itemRequestClient.findAll(from, size, userId);
    }
}
