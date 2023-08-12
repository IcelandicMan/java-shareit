package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Long itemId;
    private Long bookerId;
    private String status;
}
