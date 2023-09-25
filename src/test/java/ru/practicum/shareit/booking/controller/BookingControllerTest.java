package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private Booking booking1;
    private Booking booking2;

    private BookingRequestDto bookingDtoRequest;
    private BookingResponseDto bookingDto;

    @BeforeEach
    void beforeEach() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("NameUser1");
        user1.setEmail("user1@email.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("NameUser2");
        user2.setEmail("user2@email.com");

        User user3 = new User();
        user3.setId(3L);
        user3.setName("NameUser3");
        user3.setEmail("user3@email.com");

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("NameItem1");
        item1.setDescription("Description item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("NameItem2");
        item2.setDescription("Description item2");
        item2.setAvailable(true);
        item2.setOwner(user2);

        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(LocalDateTime.now().plusMinutes(15));
        booking1.setEnd(LocalDateTime.now().plusMinutes(30));
        booking1.setItem(item1);
        booking1.setBooker(user3);
        booking1.setStatus(BookingStatus.WAITING);

        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now());
        booking2.setItem(item1);
        booking2.setBooker(user3);
        booking2.setStatus(BookingStatus.APPROVED);

        bookingDto = BookingMapper.bookingToBookingResponseDto(booking1);

        bookingDtoRequest = new BookingRequestDto();
        bookingDtoRequest.setItemId(1L);
        bookingDtoRequest.setStart(booking1.getStart());
        bookingDtoRequest.setEnd(booking1.getEnd());
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingRequestDto.class))).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).createBooking(3L, bookingDtoRequest);
    }

    @Test
    void updateBookingTest() throws Exception {
        when(bookingService.approveOrRejectBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).approveOrRejectBooking(1L, 1L, true);
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).getBooking(1L, 1L);
    }

    @Test
    void findBookingsByBookerAndStatusTest() throws Exception {
        when(bookingService.getAllBookingsByBooker(anyLong(), anyString(), anyInt(), anyInt())).thenAnswer(invocation -> {
            List<Booking> bookings = new ArrayList<>();
            bookings.add(booking1);
            bookings.add(booking2);
            return bookings;
        });
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking1, booking2))));
    }

    @Test
    void findBookingsByOwnerAndStatusTest() throws Exception {
        when(bookingService.getAllBookingsByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenAnswer(invocation -> {
            List<Booking> bookings = new ArrayList<>();
            bookings.add(booking1);
            bookings.add(booking2);
            return bookings;
        });
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking1, booking2))));
    }
}

