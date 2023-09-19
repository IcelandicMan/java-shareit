package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.errors.BookingNotFoundException;
import ru.practicum.shareit.booking.errors.StateNotAvailableException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exeption.ItemNotAvailableException;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto createBooking(Long bookerId, BookingRequestDto booking) {
        User booker = getUserIfExist(bookerId);
        Item item = getItemIfExist(booking.getItemId());
        if (bookerId.equals(item.getOwner().getId())) {
            throw new UserNotFoundException(String.format("Пользователь с id %s является владельцем данной вещи " +
                    "с id %s и не может её бронировать", bookerId, item.getOwner().getId()));
        }
        Booking createdBooking = BookingMapper.bookingookingRequestDtotoBooking(booking, booker, item);
        if (!createdBooking.getItem().getAvailable()) {
            throw new ItemNotAvailableException("Бронирование вещи не возможно, так как её статус 'Недоступен'");
        }
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.save(createdBooking));
    }

    @Override
    public BookingResponseDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingIfExist(bookingId);
        Item item = booking.getItem();
        isUserItemOwner(userId, item);
        if (approved && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ItemNotAvailableException("Бронирование уже подтверждено владельцем");
        } else if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.bookingToBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        Booking booking = getBookingIfExist(bookingId);
        isUserBookerOrItemOwner(userId, booking);
        return BookingMapper.bookingToBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByBooker(Long userId, String stateString, Integer from, Integer size) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new StateNotAvailableException("Unknown state: UNSUPPORTED_STATUS");
        }

        User user = getUserIfExist(userId);
        Pageable pageable = PageRequest.of(from / size, size);

        List<Booking> bookingList = new ArrayList<>();

        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByBookerId(userId, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findCurrentBookingsByBookerId(userId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findPastBookingsByBookerId(userId, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findFutureBookingsByBookerId(userId, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findWaitingBookingsByBookerId(userId, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findRejectedBookingsByBookerId(userId, pageable);
                break;
        }
        return bookingList.stream()
                .map(BookingMapper::bookingToBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByOwner(Long userId, String stateString, Integer from, Integer size) {
        BookingState state;
        try {
            state = BookingState.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
        Pageable pageForOwner = PageRequest.of(0, 1);
        Pageable pageable = PageRequest.of(from / size, size);
        User user = getUserIfExist(userId);
        if (itemRepository.findAllByOwnerId(userId, pageForOwner).isEmpty()) {
            throw new ItemNotFoundException(String.format("У пользователя c id %s не найдено ни одной вещи", userId));
        }

        List<Booking> bookingList = new ArrayList<>();

        switch (state) {
            case ALL:
                bookingList = bookingRepository.findByOwner(userId, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findCurrentBookingsByOwner(userId, pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findPastBookingsByOwner(userId, pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findFutureBookingsByOwner(userId, pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findWaitingBookingsByOwner(userId, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findRejectedBookingsByOwner(userId, pageable);
                break;
        }
        return bookingList.stream()
                .map(BookingMapper::bookingToBookingResponseDto)
                .collect(Collectors.toList());
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

    private Booking getBookingIfExist(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new BookingNotFoundException(String.format("Бронирование  c id %s не найдена", bookingId));
        }
        return booking.get();
    }

    private void isUserItemOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemNotFoundException(String.format("Вещь c id %s не принадлежит пользователю с id %s",
                    item.getId(), userId));
        }
    }

    private void isUserBookerOrItemOwner(Long userId, Booking booking) {
        User booker = booking.getBooker();
        Item item = booking.getItem();
        User itemOwner = item.getOwner();
        if (!booker.getId().equals(userId) && !itemOwner.getId().equals(userId)) {
            throw new BookingNotFoundException(String.format("Пользователь с id %s не имеет прав просматривать " +
                    "бронирование с id %s", userId, booking.getId()));
        }
    }
}
