package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    public static final String BOOKING_INVALID_MESSAGE = "id бронирования не должно быть пустым";
    public static final String USER_INVALID_MESSAGE = "id пользователя не должен быть пустым";

    @PostMapping()
    public ResponseEntity<Object> createBooing(@NotNull(message = USER_INVALID_MESSAGE)
                                               @RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestBody @Valid BookingRequestDto booking) {
        log.info("Запрошено создание бронирования: {} от пользователя под id {} ", booking, userId);
        return bookingClient.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrRejectBooking(@NotNull(message = USER_INVALID_MESSAGE)
                                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @NotNull(message = BOOKING_INVALID_MESSAGE)
                                                         @PathVariable Long bookingId,
                                                         @RequestParam Boolean approved) {
        log.info("Запрошено обновление статуса бронирования с id {} от пользователя под id {}", bookingId, userId);
        return bookingClient.patchBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@NotNull(message = USER_INVALID_MESSAGE)
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @NotNull(message = BOOKING_INVALID_MESSAGE)
                                             @PathVariable Long bookingId) {
        log.info("Запрошено бронирование под id {} от пользователя с id {}", bookingId, userId);
        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getBookingsByBooker(@NotNull(message = USER_INVALID_MESSAGE)
                                                      @RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @PositiveOrZero(message = "ошибка в параметре 'from")
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive(message = "ошибка в параметре 'size")
                                                      @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Запрошены все бронирования пользователя с id {} с параметром state {}, from {}, size {}",
                userId, state, from, size);
        return bookingClient.findAllByBooker(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByItemOwner(@NotNull(message = USER_INVALID_MESSAGE)
                                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @PositiveOrZero(message = "ошибка в параметре 'from")
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive(message = "ошибка в параметре 'size")
                                                         @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Запрошены все бронирования владельца вещей с id {} с параметром state {}, from {}, size {}",
                userId, state, from, size);
        return bookingClient.findAllByItemOwner(state, userId, from, size);
    }
}
