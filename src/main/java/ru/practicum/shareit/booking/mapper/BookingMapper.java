package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingResponseDto bookingToBookingResponseDto(Booking booking) {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(booking.getId());
        responseDto.setStart(booking.getStart());
        responseDto.setEnd(booking.getEnd());
        responseDto.setStatus(booking.getStatus());

        User booker = new User();
        booker.setId(booking.getBooker().getId());
        responseDto.setBooker(booker);

        Item item = new Item();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        responseDto.setItem(item);

        return responseDto;
    }

    public static Booking bookingookingRequestDtotoBooking(BookingRequestDto booking, User booker, Item item) {
        Booking createdBooking = new Booking();
        createdBooking.setStart(booking.getStart());
        createdBooking.setEnd(booking.getEnd());
        createdBooking.setBooker(booker);
        createdBooking.setItem(item);
        createdBooking.setStatus(BookingStatus.WAITING);
        return createdBooking;
    }
}

