package ru.practicum.shareit.itemRequest.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.itemRequest.dto.ItemRequestRequestedDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponsedDto;
import ru.practicum.shareit.itemRequest.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.itemRequest.mapper.ItemRequestMapper;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestResponsedDto createItemRequest(Long requestorId, ItemRequestRequestedDto itemRequest) {
        ItemRequest createdItemRequest = ItemRequestMapper.itemRequestResponsedDtoToItemRequest(itemRequest);
        createdItemRequest.setCreated(LocalDateTime.now());
        User user = getUserIfExist(requestorId);
        createdItemRequest.setRequestor(user);
        return ItemRequestMapper.itemRequestToitemRequestResponsedDto(itemRequestRepository.save(createdItemRequest));
    }

    @Override
    public ItemRequestResponsedDto getItemRequest(Long userId, Long itemRequestId) {
        User user = getUserIfExist(userId);
        ItemRequest itemRequest = getItemRequestIfExist(itemRequestId);
        return ItemRequestMapper.itemRequestToitemRequestResponsedDto(itemRequest);
    }

    @Override
    public List<ItemRequestResponsedDto> getAllItemRequestByOwner(Long userId, Integer from, Integer size) {
        User user = getUserIfExist(userId);
        List<ItemRequest> itemRequests = Collections.emptyList();
        Pageable pageable = PageRequest.of(from / size, size);
        itemRequests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId, pageable);
        return itemRequests.stream()
                .map(ItemRequestMapper::itemRequestToitemRequestResponsedDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponsedDto> getAllItemRequest(Long userId, Integer from, Integer size) {
        User user = getUserIfExist(userId);
        List<ItemRequest> itemRequests = Collections.emptyList();
        Pageable pageable = PageRequest.of(from / size, size);
        itemRequests = itemRequestRepository.findAllByCreatedDesc(userId, pageable);
        return itemRequests.stream()
                .map(ItemRequestMapper::itemRequestToitemRequestResponsedDto)
                .collect(Collectors.toList());
    }

    private User getUserIfExist(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть null");
        }
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь c id %s не найден", userId));
        }
        return userOptional.get();
    }

    private ItemRequest getItemRequestIfExist(Long itemRequestId) {
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(itemRequestId);
        if (itemRequestOptional.isEmpty()) {
            throw new ItemRequestNotFoundException(String.format("Запрос на вещь c id %s не найден", itemRequestId));
        }
        return itemRequestOptional.get();
    }


}
