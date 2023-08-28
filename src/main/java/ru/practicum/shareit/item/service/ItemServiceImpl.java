package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exeption.ItemNotAvailableException;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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

    @Override
    public ItemResponseDto createItem(Long userId, ItemRequestDto item) {
        User owner = getUserIfExist(userId);
        Item createdItem = ItemMapper.itemRequestDtoToItem(item);
        createdItem.setOwner(owner);
        return createItemResponseDto(userId, itemRepository.save(createdItem));
    }

    @Override
    public ItemResponseDto getItem(Long userId, Long itemId) {
        Item item = getItemIfExist(itemId);
        return createItemResponseDto(userId, item);
    }

    @Override
    public List<ItemResponseDto> getItems(Long userId) {
        List<Item> items;
        if (userId == null) {
            items = itemRepository.findAll();
        }
        User owner = getUserIfExist(userId);
        items = itemRepository.findAllByOwnerId(userId);
        return items.stream()
                .map(item -> createItemResponseDto(userId, item))
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
        return createItemResponseDto(userId, itemRepository.save(item));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        User user = getUserIfExist(userId);
        Item item = getItemIfExist(itemId);
        isUserOwner(user.getId(), item);
        itemRepository.delete(item);
    }

    @Override
    public List<ItemResponseDto> searchItems(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> foundItems = itemRepository.findAllBySearching(text);
        return foundItems.stream()
                .map(item -> createItemResponseDto(userId, item))
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
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ItemNotFoundException(String.format("Пользователь c id %s не найден", userId));
        }
        return user.get();
    }

    private void isUserOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemNotFoundException(String.format("Вещь c id %s не принадлежит пользователю с id %s",
                    item.getId(), userId));
        }
    }


    private ItemResponseDto createItemResponseDto(Long userId, Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        List<Comment> commentList = commentRepository.findCommentsByItemId(item.getId());
        List<CommentResponseDto> commentResponseDtoList = commentList.stream()
                .map(CommentMapper::commentToCommentResponse)
                .collect(Collectors.toList());
        itemResponseDto.setComments(commentResponseDtoList);

        if (userId.equals(item.getOwner().getId())) {
            List<Booking> lastBookingList = bookingRepository.findNearestBookingBeforeCurrentTimeForItemId(item.getId());

            if (!lastBookingList.isEmpty()) {
                Booking lastBooking = lastBookingList.get(0);
                User user = lastBooking.getBooker();
                User booker = new User();
                booker.setId(user.getId());

                BookingForItemDto lastBookingDto = new BookingForItemDto();
                lastBookingDto.setId(lastBooking.getId());
                lastBookingDto.setStart(lastBooking.getStart());
                lastBookingDto.setEnd(lastBooking.getEnd());
                lastBookingDto.setBookerId(lastBooking.getBooker().getId());

                itemResponseDto.setLastBooking(lastBookingDto);
            }

            List<Booking> nextBookingList = bookingRepository.findNextBookingAfterCurrentTimeForItemId(item.getId());
            if (!nextBookingList.isEmpty()) {
                Booking nextBooking = nextBookingList.get(0);
                User user = nextBooking.getBooker();
                User booker = new User();
                booker.setId(user.getId());

                BookingForItemDto nextBookingDto = new BookingForItemDto();

                nextBookingDto.setId(nextBooking.getId());
                nextBookingDto.setStart(nextBooking.getStart());
                nextBookingDto.setEnd(nextBooking.getEnd());
                nextBookingDto.setBookerId(nextBooking.getBooker().getId());

                itemResponseDto.setNextBooking(nextBookingDto);
            }
        }

        return itemResponseDto;
    }
}

