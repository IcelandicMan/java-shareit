package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exeption.ItemNotAvailableException;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemrequest.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.itemrequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemSerVice {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemResponseDto createItem(Long userId, ItemRequestDto item) {
        User owner = getUserIfExist(userId);
        Item createdItem = ItemMapper.itemRequestDtoToItem(item);
        if (item.getRequestId() != null) {
            ItemRequest itemRequest = getItemRequestIfExist(item.getRequestId());
            createdItem.setItemRequest(itemRequest);
        }
        createdItem.setOwner(owner);
        List<Comment> commentList = commentRepository.findCommentsByItemId(createdItem.getId());
        List<Booking> lastBookingList = bookingRepository.findNearestBookingBeforeCurrentTimeForItemId(createdItem.getId());
        List<Booking> nextBookingList = bookingRepository.findNextBookingAfterCurrentTimeForItemId(createdItem.getId());
        return ItemMapper.itemToItemResponseDto(userId, itemRepository.save(createdItem), commentList, lastBookingList, nextBookingList);
    }

    @Override
    public ItemResponseDto getItem(Long userId, Long itemId) {
        Item item = getItemIfExist(itemId);
        List<Comment> commentList = commentRepository.findCommentsByItemId(item.getId());
        List<Booking> lastBookingList = bookingRepository.findNearestBookingBeforeCurrentTimeForItemId(item.getId());
        List<Booking> nextBookingList = bookingRepository.findNextBookingAfterCurrentTimeForItemId(item.getId());
        return ItemMapper.itemToItemResponseDto(userId, item, commentList, lastBookingList, nextBookingList);
    }

    @Override
    public List<ItemResponseDto> getItems(Long userId, Integer from, Integer size) {
        List<Item> items;
        Pageable pageable = PageRequest.of(from / size, size);
        if (userId == null) {
            items = itemRepository.findAll(pageable).getContent();
        } else {
            User owner = getUserIfExist(userId);
            items = itemRepository.findAllByOwnerId(userId, pageable);
        }
        return items.stream()
                .map(item -> {
                    List<Comment> commentList = commentRepository.findCommentsByItemId(item.getId());
                    List<Booking> lastBookingList = bookingRepository.findNearestBookingBeforeCurrentTimeForItemId(item.getId());
                    List<Booking> nextBookingList = bookingRepository.findNextBookingAfterCurrentTimeForItemId(item.getId());
                    return ItemMapper.itemToItemResponseDto(item.getOwner().getId(), item, commentList, lastBookingList, nextBookingList);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto updateItem(Long userId, Long itemId, ItemRequestDto updatedItem) {
        Item item = getItemIfExist(itemId);
        User user = getUserIfExist(userId);
        isUserOwner(user.getId(), item);

        final String name = updatedItem.getName();
        final String description = updatedItem.getDescription();
        final Boolean available = updatedItem.getAvailable();

        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }
        List<Comment> commentList = commentRepository.findCommentsByItemId(item.getId());
        List<Booking> lastBookingList = bookingRepository.findNearestBookingBeforeCurrentTimeForItemId(item.getId());
        List<Booking> nextBookingList = bookingRepository.findNextBookingAfterCurrentTimeForItemId(item.getId());
        return ItemMapper.itemToItemResponseDto(userId, itemRepository.save(item), commentList, lastBookingList, nextBookingList);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        User user = getUserIfExist(userId);
        Item item = getItemIfExist(itemId);
        isUserOwner(user.getId(), item);
        itemRepository.delete(item);
    }

    @Override
    public List<ItemResponseDto> searchItems(Long userId, String text, Integer from, Integer size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> foundItems = itemRepository.findAllBySearching(text, pageable);
        return foundItems.stream()
                .map(item -> {
                    List<Comment> commentList = commentRepository.findCommentsByItemId(item.getId());
                    List<Booking> lastBookingList = bookingRepository.findNearestBookingBeforeCurrentTimeForItemId(item.getId());
                    List<Booking> nextBookingList = bookingRepository.findNextBookingAfterCurrentTimeForItemId(item.getId());
                    return ItemMapper.itemToItemResponseDto(userId, item, commentList, lastBookingList, nextBookingList);
                })
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto createComment(Long userId, Long itemId, CommentRequestDto comment) {
        User user = getUserIfExist(userId);
        Item item = getItemIfExist(itemId);
        if (userId.equals(item.getOwner().getId())) {
            throw new ItemNotAvailableException(String.format("Пользователь с id %s является владельцем вещи" +
                    "и не может оставлять комментарий", userId));
        }
        if (bookingRepository.findPastBookingsByBookerIdAndItemId(userId, itemId).isEmpty()) {
            throw new ItemNotAvailableException(String.format("Пользователь с id %s еще ни разу не бронировал вещь c id %s " +
                    "и не может оставлять комментарий", userId, itemId));
        }

        Comment createdcomment = CommentMapper.commentRequestDtoToComment(comment);
        createdcomment.setAuthor(user);
        createdcomment.setItem(item);
        return CommentMapper.commentToCommentResponse(commentRepository.save(createdcomment));
    }


    private Item getItemIfExist(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Вещь c id %s не найдена", itemId));
        }
        return item.get();
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

    private void isUserOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemNotFoundException(String.format("Вещь c id %s не принадлежит пользователю с id %s",
                    item.getId(), userId));
        }
    }

    private ItemRequest getItemRequestIfExist(Long itemRequestId) {
        Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(itemRequestId);
        if (itemRequestOptional.isEmpty()) {
            throw new ItemRequestNotFoundException(String.format("Запрос на вещь c id %s не найден", itemRequestId));
        }
        return itemRequestOptional.get();
    }
}

