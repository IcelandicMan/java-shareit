package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemSerVice;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItServer.class, UserServiceImpl.class})
@TestPropertySource(properties = {"db.name=test"})
@Transactional
class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemSerVice itemSerVice;
    private final BookingService bookingService;

    private UserRequestDto userRequestDto;
    private UserRequestDto userRequestDto2;
    private ItemRequestDto itemRequestDto1;
    private BookingRequestDto bookingRequestDto;

    private BookingRequestDto bookingRequestDto2;

    @BeforeEach
    void beforeEach() {

        userRequestDto = new UserRequestDto();
        userRequestDto.setName("Arthur");
        userRequestDto.setEmail("arthur@gmail.com");

        userRequestDto2 = new UserRequestDto();
        userRequestDto2.setName("Jonsi");
        userRequestDto2.setEmail("jonsi@gmail.com");

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setName("NameItem1");
        itemRequestDto1.setDescription("Description item1");
        itemRequestDto1.setAvailable(true);
    }


    @Test
    void createBooking() {

        UserResponseDto user1 = userService.createUser(userRequestDto);
        UserResponseDto user2 = userService.createUser(userRequestDto2);
        ItemResponseDto item = itemSerVice.createItem(user1.getId(), itemRequestDto1);

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(5));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(10));

        Long id = bookingService.createBooking(user2.getId(), bookingRequestDto).getId();

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking queryItem = query
                .setParameter("id", id)
                .getSingleResult();
        assertNotNull(queryItem);
        assertEquals(BookingStatus.WAITING, queryItem.getStatus());
    }


    @Test
    void getItemsTest() {
        UserResponseDto user1 = userService.createUser(userRequestDto);
        UserResponseDto user2 = userService.createUser(userRequestDto2);
        ItemResponseDto item = itemSerVice.createItem(user1.getId(), itemRequestDto1);

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(5));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(10));

        bookingRequestDto2 = new BookingRequestDto();
        bookingRequestDto2.setItemId(item.getId());
        bookingRequestDto2.setStart(LocalDateTime.now().plusDays(100));
        bookingRequestDto2.setEnd(LocalDateTime.now().plusDays(150));

        bookingService.createBooking(user2.getId(), bookingRequestDto);
        bookingService.createBooking(user2.getId(), bookingRequestDto2);
        List<Booking> allIBookings = em.createQuery("SELECT b FROM Booking b", Booking.class)
                .getResultList();

        assertEquals(2, allIBookings.size());
    }
}

