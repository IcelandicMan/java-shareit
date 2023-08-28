package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingRequestDto {

    private Long itemId;
    @NotNull
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull
    @Future(message = "Дата окончания бронирования не может быть в прошлом")
    private LocalDateTime end;

    @AssertTrue(message = "Дата окончания бронирования должна быть после даты начала и не равна ей")
    private boolean isValidBookingPeriod() {
        return end == null || start == null || end.isAfter(start);
    }

}

