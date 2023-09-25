package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User user;
    private User user2;

    private Item item;

    private Booking booking;
    private Booking booking2;
    PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        pageRequest = PageRequest.of(0, 5);
        user = new User();
        user.setName("Arthur");
        user.setEmail("arthur@gmail.com");

        user2 = new User();
        user2.setName("Jonsi");
        user2.setEmail("jonsi@gmail.com");

        userRepository.save(user);
        userRepository.save(user2);

        item = new Item();
        item.setName("Укулеле");
        item.setDescription("струны карбоновые");
        item.setOwner(user2);
        item.setAvailable(true);

        itemRepository.save(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(4));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);

        booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusDays(5));
        booking2.setEnd(LocalDateTime.now().plusDays(10));
        booking2.setItem(item);
        booking2.setBooker(user);
        booking2.setStatus(BookingStatus.APPROVED);

        bookingRepository.save(booking);
        bookingRepository.save(booking2);
    }

    @Test
    void findByBookerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findByBookerId(1L, pageRequest);
        assertEquals(2, bookings.size());
        assertEquals(booking, bookings.get(1));
        assertEquals(booking2, bookings.get(0));
    }


    @Test
    void testFindNearestBookingBeforeCurrentTimeForItemId() {
        Long itemId = 1L;
        List<Booking> nearestBookings = bookingRepository.findNearestBookingBeforeCurrentTimeForItemId(itemId);

        assertEquals(1, nearestBookings.size());

        assertEquals(nearestBookings.get(0), booking);
    }

    @Test
    void findLastBookingsForItems() {
        Long itemId = 1L;
        List<Booking> bookings = bookingRepository.findNextBookingAfterCurrentTimeForItemId(itemId);
        assertEquals(1, bookings.size());
        assertEquals(booking2, bookings.get(0));
    }

}