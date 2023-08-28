package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public BookingResponseDto createBooing(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody @Valid BookingRequestDto booking) {
        log.info("Запрошено создание бронирования: {} от пользователя под id {} ", booking, userId);
        BookingResponseDto createdBooking = bookingService.createBooking(userId, booking);
        log.info("Запрос на создание бронирования от пользователя c id {} выполнен," +
                "бронирование создано: {} ", userId, createdBooking);
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveOrRejectBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("Запрошено обновление статуса бронирования с id {} от пользователя под id {}", bookingId, userId);
        BookingResponseDto booking = bookingService.approveOrRejectBooking(userId, bookingId, approved);
        log.info("Обновление статуса бронирования с id {} от пользователя под id {} выполнено", bookingId, userId);
        return booking;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Запрошено бронирование под id {} от пользователя с id {}", bookingId, userId);
        BookingResponseDto booking = bookingService.getBooking(userId, bookingId);
        log.info("Запрос на предоставление бронирования под id {} от пользователя  с id {} выполнен", bookingId, userId);
        return booking;
    }

    @GetMapping()
    public List<BookingResponseDto> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрошены все бронирования пользователя с id {} с параметром state {}", userId, state);
        List<BookingResponseDto> bookings = bookingService.getAllBookingsByBooker(userId, state);
        log.info("Запрос на предоставление бронирований пользователя с id {} с параметром {} выполнен ", userId, state);
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByItemOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрошены все бронирования владельца вещей с id {} с параметром state {}", userId, state);
        List<BookingResponseDto> bookings = bookingService.getAllBookingsByOwner(userId, state);
        log.info("Запрос на предоставление бронирований  владельца вещей с id {} с параметром {} выполнен ", userId, state);
        return bookings;
    }


}
