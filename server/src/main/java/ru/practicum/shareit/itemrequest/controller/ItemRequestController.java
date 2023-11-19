package ru.practicum.shareit.itemrequest.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestedDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestResponsedDto;
import ru.practicum.shareit.itemrequest.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {


    private final ItemRequestService itemRequestService;


    @PostMapping()
    public ItemRequestResponsedDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestBody ItemRequestRequestedDto itemRequest) {
        log.info("Запрошено создание запроса вещи: {} от пользователя под id {} ", itemRequest, userId);
        ItemRequestResponsedDto createdItemRequest = itemRequestService.createItemRequest(userId, itemRequest);
        log.info("Запрос на создание запроса вещи от пользователя c id {} выполнен, запрос создан: {} ", userId, createdItemRequest);
        return createdItemRequest;
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponsedDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long requestId) {
        log.info("Запрошен Запрос вещи под id {} от пользователя с id {}", requestId, userId);
        ItemRequestResponsedDto itemRequest = itemRequestService.getItemRequest(userId, requestId);
        log.info("Запрос на предоставление Запроса вещи под id {} от пользователя  с id {} выполнен", requestId, userId);
        return itemRequest;
    }


    @GetMapping()
    public List<ItemRequestResponsedDto> getAllItemRequestByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                 @RequestParam(name = "from") Integer from,
                                                                 @RequestParam(name = "size") Integer size) {
        log.info("Запрошены все запросы на вещи от пользователя с id {} с параметрами страницы from {} size {} ",
                userId, from, size);
        List<ItemRequestResponsedDto> itemRequests = itemRequestService.getAllItemRequestByOwner(userId, from, size);
        log.info("Запрос на предоставление всех запросов на вещи с id {} с параметрами страницы from {} size {} выполнен ",
                userId, from, size);
        return itemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestResponsedDto> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(name = "from") Integer from,
                                                           @RequestParam(name = "size") Integer size) {
        log.info("Запрошены все запросы на вещи от пользователя с id {} с параметрами страницы from {} size {} ",
                userId, from, size);
        List<ItemRequestResponsedDto> itemRequests = itemRequestService.getAllItemRequest(userId, from, size);
        log.info("Запрос на предоставление всех запросов на вещи с id {} с параметрами страницы from {} size {} выполнен ",
                userId, from, size);
        return itemRequests;
    }
}
