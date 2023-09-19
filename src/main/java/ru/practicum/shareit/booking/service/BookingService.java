package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Long bookerId, BookingRequestDto booking);

    BookingResponseDto getBooking(Long userId, Long bookingId);

    BookingResponseDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved);

    List<BookingResponseDto> getAllBookingsByBooker(Long userId, String state, Integer from, Integer size);

    List<BookingResponseDto> getAllBookingsByOwner(Long userId, String state, Integer from, Integer size);
}
