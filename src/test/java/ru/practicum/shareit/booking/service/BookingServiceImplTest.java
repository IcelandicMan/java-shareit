package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.errors.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exeption.ItemNotAvailableException;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;


    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;

    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    private BookingRequestDto bookingDtoRequest;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        pageRequest = PageRequest.of(0, 5);
        user1 = new User();
        user1.setId(1L);
        user1.setName("NameUser1");
        user1.setEmail("user1@email.com");

        user2 = new User();
        user2.setId(2L);
        user2.setName("NameUser2");
        user2.setEmail("user2@email.com");

        user3 = new User();
        user3.setId(3L);
        user3.setName("NameUser3");
        user3.setEmail("user3@email.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Тестовый запрос");
        itemRequest.setCreated(LocalDateTime.now());

        item1 = new Item();
        item1.setId(1L);
        item1.setName("NameItem1");
        item1.setDescription("Description item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        item2 = new Item();
        item2.setId(2L);
        item2.setName("NameItem2");
        item2.setDescription("Description item2");
        item2.setAvailable(false);
        item2.setOwner(user2);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(user1);
        comment.setCreated(LocalDateTime.now());
        comment.setText("Text");

        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(LocalDateTime.now().plusMinutes(10));
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

        booking3 = new Booking();
        booking3.setId(3L);
        booking3.setStart(LocalDateTime.now().plusDays(12));
        booking3.setEnd(LocalDateTime.now().plusDays(20));
        booking3.setItem(item1);
        booking3.setBooker(user3);
        booking3.setStatus(BookingStatus.APPROVED);


        bookingDtoRequest = new BookingRequestDto();
        bookingDtoRequest.setItemId(booking1.getItem().getId());
        bookingDtoRequest.setStart(booking1.getStart());
        bookingDtoRequest.setEnd(booking1.getEnd());
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        BookingResponseDto result = bookingService.createBooking(user3.getId(), bookingDtoRequest);

        assertNotNull(result);
        assertEquals(booking1.getId(), result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(booking1.getBooker().getName(), user3.getName());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }


    @Test
    void createBookingWithWrongUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(1L, bookingDtoRequest));
    }

    @Test
    void createBookingWithNotExistItemTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> bookingService.createBooking(1L, bookingDtoRequest));
    }

    @Test
    void createBookingWhenBookerIsOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(1L, bookingDtoRequest));
    }

    @Test
    void createBookingWhenStatusNotAvailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item2));

        assertThrows(ItemNotAvailableException.class, () -> bookingService.createBooking(1L, bookingDtoRequest));
    }

    @Test
    void approveBookingWhenBookingNotExistTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () -> bookingService.approveOrRejectBooking(user1.getId(), booking1.getId(), true));
    }

    @Test
    void approveBookingTestWhenStatusAlreadyApprovedTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));
        assertThrows(ItemNotAvailableException.class, () -> bookingService.approveOrRejectBooking(user1.getId(), booking2.getId(), true));
    }

    @Test
    void approveBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking bookingToSave = invocation.getArgument(0);
            bookingToSave.setStatus(BookingStatus.APPROVED);
            return bookingToSave;
        });
        BookingResponseDto bookingResponseDto = bookingService.approveOrRejectBooking(user1.getId(), booking1.getId(), true);

        assertEquals(bookingResponseDto.getStatus(), BookingStatus.APPROVED);
        assertEquals(bookingResponseDto.getId(), booking1.getId());
    }

    @Test
    void getBookingFromOwnerAndBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        BookingResponseDto bookingResponseDto = bookingService.getBooking(user1.getId(), booking1.getId());

        assertEquals(bookingResponseDto.getId(), booking1.getId());
        assertEquals(bookingResponseDto.getStatus(), BookingStatus.WAITING);

        bookingResponseDto = bookingService.getBooking(user3.getId(), booking1.getId());

        assertEquals(bookingResponseDto.getId(), booking1.getId());
        assertEquals(bookingResponseDto.getStatus(), BookingStatus.WAITING);
    }

    @Test
    void getBookingNotFromOwnerAndBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBooking(user2.getId(), booking1.getId()));
    }

    @Test
    void getAllBookingsByOwnerWithWrongStateTest() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.getAllBookingsByOwner(user3.getId(), "Ku", 0, 5));
    }

    @Test
    void getAllBookingsByOwnerWithoutItemsTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThrows(ItemNotFoundException.class, () -> bookingService.getAllBookingsByOwner(user1.getId(), "ALL", 0, 5));
    }

    @Test
    void getAllBookingsByOwnerWithStateTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(anyLong(), any(PageRequest.class))).thenReturn(List.of(item1));
        when(bookingRepository.findByOwner(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking1, booking2));

        List<BookingResponseDto> bookingList = bookingService.getAllBookingsByOwner(user1.getId(), "ALL", 0, 5);
        assertEquals(2, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 1);
        assertEquals(bookingList.get(1).getId(), 2);

        when(bookingRepository.findCurrentBookingsByOwner(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking2));
        bookingList = bookingService.getAllBookingsByOwner(user1.getId(), "CURRENT", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 2);

        when(bookingRepository.findPastBookingsByOwner(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking1));
        bookingList = bookingService.getAllBookingsByOwner(user1.getId(), "PAST", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 1);


        when(bookingRepository.findFutureBookingsByOwner(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking3));
        bookingList = bookingService.getAllBookingsByOwner(user1.getId(), "FUTURE", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 3);

        when(bookingRepository.findWaitingBookingsByOwner(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking1));
        bookingList = bookingService.getAllBookingsByOwner(user1.getId(), "WAITING", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 1);
        assertEquals(bookingList.get(0).getStatus(), BookingStatus.WAITING);


        when(bookingRepository.findRejectedBookingsByOwner(anyLong(), any(PageRequest.class))).thenAnswer(invocation -> {
            booking1.setStatus(BookingStatus.REJECTED);
            return List.of(booking1);
        });

        bookingList = bookingService.getAllBookingsByOwner(user1.getId(), "REJECTED", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 1);
        assertEquals(bookingList.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void getAllBookingsByBookerWithoutUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingsByBooker(user3.getId(), "ALL", 0, 5));
    }

    @Test
    void getAllBookingsByBookerWithStateTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user3));
        when(bookingRepository.findByBookerId(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking1, booking2, booking3));
        List<BookingResponseDto> bookingList = bookingService.getAllBookingsByBooker(user3.getId(), "ALL", 0, 5);
        assertEquals(3, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 1);
        assertEquals(bookingList.get(1).getId(), 2);
        assertEquals(bookingList.get(2).getId(), 3);

        when(bookingRepository.findCurrentBookingsByBookerId(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking2));
        bookingList = bookingService.getAllBookingsByBooker(user3.getId(), "CURRENT", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 2);

        when(bookingRepository.findPastBookingsByBookerId(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking1));
        bookingList = bookingService.getAllBookingsByBooker(user3.getId(), "PAST", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 1);

        when(bookingRepository.findFutureBookingsByBookerId(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking3));
        bookingList = bookingService.getAllBookingsByBooker(user3.getId(), "FUTURE", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 3);

        when(bookingRepository.findWaitingBookingsByBookerId(anyLong(), any(PageRequest.class))).thenReturn(List.of(booking1));
        bookingList = bookingService.getAllBookingsByBooker(user3.getId(), "WAITING", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 1);

        when(bookingRepository.findRejectedBookingsByBookerId(anyLong(), any(PageRequest.class))).thenAnswer(invocation -> {
            booking1.setStatus(BookingStatus.REJECTED);
            return List.of(booking1);
        });
        bookingList = bookingService.getAllBookingsByBooker(user3.getId(), "REJECTED", 0, 5);
        assertEquals(1, bookingList.size());
        assertEquals(bookingList.get(0).getId(), 1);
    }
}