package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.exeption.ItemNotAvailableException;
import ru.practicum.shareit.item.exeption.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.itemrequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Item item3;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;
    private CommentRequestDto commentRequestDto;
    private Booking booking;
    private Booking booking2;
    private Comment comment;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {

        user1 = new User();
        user1.setId(1L);
        user1.setName("NameUser1");
        user1.setEmail("user1@email.com");

        user2 = new User();
        user2.setId(2L);
        user2.setName("NameUser2");
        user2.setEmail("user2@email.com");

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setName("NameItem1");
        itemRequestDto1.setDescription("Description item1");
        itemRequestDto1.setAvailable(true);

        itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setName("UPNameItem1");
        itemRequestDto2.setDescription("UPDescription item1");
        itemRequestDto2.setAvailable(false);

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

        item3 = new Item();
        item3.setId(3L);
        item3.setName("NameItem3");
        item3.setDescription("Description item3");
        item3.setAvailable(true);
        item3.setOwner(user1);

        commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("5 звезд");

        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user1);
        booking.setItem(item2);
        booking.setStart(LocalDateTime.now().minusDays(14));
        booking.setEnd(LocalDateTime.now().minusDays(10));

        booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setBooker(user2);
        booking2.setStart(LocalDateTime.now().plusDays(15));
        booking2.setEnd(LocalDateTime.now().plusDays(30));

        comment = new Comment();
        comment.setText("Хорошо");
        comment.setId(1L);
        comment.setItem(item1);
        comment.setAuthor(user2);
    }

    @Test
    void createItemWithoutUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.createItem(1L, itemRequestDto1));
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = new Item();
            item.setOwner(user1);
            item.setId(1L);
            item.setName(itemRequestDto1.getName());
            item.setDescription(itemRequestDto1.getDescription());
            item.setAvailable(itemRequestDto1.getAvailable());
            return item;
        });


        ItemResponseDto item = itemService.createItem(user1.getId(), itemRequestDto1);
        assertNotNull(item.getId());
        assertEquals(item.getId(), 1);
        assertEquals(item.getName(), itemRequestDto1.getName());
        assertEquals(item.getDescription(), itemRequestDto1.getDescription());
        assertTrue(item.getAvailable());
        assertNull(item.getRequestId());
        assertNull(item.getNextBooking());
        assertNull(item.getLastBooking());
        assertTrue(item.getComments().isEmpty());
    }

    @Test
    void getItemIfExistTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        ItemResponseDto item = itemService.getItem(user1.getId(), item1.getId());

        assertEquals(item.getId(), 1);
        assertEquals(item.getName(), item1.getName());
        assertEquals(item.getDescription(), item1.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void getItemIfUserOwnerTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findNearestBookingBeforeCurrentTimeForItemId(anyLong())).thenReturn(List.of(booking));
        when(bookingRepository.findNextBookingAfterCurrentTimeForItemId(anyLong())).thenReturn(List.of(booking2));
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(List.of(comment));

        ItemResponseDto item = itemService.getItem(user1.getId(), item1.getId());

        assertEquals(item.getId(), 1);
        assertEquals(item.getName(), item1.getName());
        assertEquals(item.getDescription(), item1.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(item.getNextBooking().getId(), booking2.getId());
        assertEquals(item.getLastBooking().getId(), booking.getId());
        assertEquals(item.getComments().get(0).getText(), comment.getText());
    }

    @Test
    void getItemsIfUserNotExistTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.getItems(1L, 0, 5));
    }

    @Test
    void getItemsByOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(item1, item3));

        List<ItemResponseDto> items = itemService.getItems(user1.getId(), 0, 5);
        assertEquals(items.size(), 2);
        assertEquals(items.get(0).getName(), item1.getName());
        assertEquals(items.get(1).getName(), item3.getName());
    }

    @Test
    void getItemsIfOwnerNull() {
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(item1, item2, item3), Pageable.unpaged(), 3));

        List<ItemResponseDto> items = itemService.getItems(null, 0, 5);
        assertEquals(items.size(), 3);
        assertEquals(items.get(0).getName(), item1.getName());
        assertEquals(items.get(1).getName(), item2.getName());
        assertEquals(items.get(2).getName(), item3.getName());
    }

    @Test
    void updateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            item1.setAvailable(itemRequestDto2.getAvailable());
            item1.setName(itemRequestDto2.getName());
            item1.setDescription(itemRequestDto2.getDescription());
            return item1;
        });

        ItemResponseDto item = itemService.updateItem(user1.getId(), item1.getId(), itemRequestDto2);

        assertEquals(item.getId(), item1.getId());
        assertEquals(item.getName(), itemRequestDto2.getName());
        assertEquals(item.getAvailable(), itemRequestDto2.getAvailable());
        assertEquals(item.getDescription(), itemRequestDto2.getDescription());
    }

    @Test
    void updateItemWhenUserNotOwnerTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(2L, 1L, itemRequestDto2));
    }

    @Test
    void searchItemsIfEmptyText() {
        List<ItemResponseDto> items = itemService.searchItems(1L, null, 0, 5);

        assertTrue(items.isEmpty());
    }

    @Test
    void searchItemsText() {
        when(itemRepository.findAllBySearching(anyString(), any(Pageable.class))).thenReturn(List.of(item1, item2));

        List<ItemResponseDto> items = itemService.searchItems(1L, "Name", 0, 5);

        assertEquals(items.size(), 2);
        assertEquals(items.get(0).getName(), item1.getName());
        assertEquals(items.get(1).getName(), item2.getName());
    }

    @Test
    void createCommentFromOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThrows(ItemNotAvailableException.class, () -> itemService.createComment(user1.getId(), item1.getId(), commentRequestDto));
    }

    @Test
    void createCommentWithoutBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item2));

        assertThrows(ItemNotAvailableException.class, () -> itemService.createComment(user1.getId(), item2.getId(), commentRequestDto));
    }

    @Test
    void createCommentWithBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item2));
        when(bookingRepository.findPastBookingsByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(List.of(booking));
        when(bookingRepository.findPastBookingsByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = new Comment();
            comment.setId(1L);
            comment.setAuthor(user1);
            comment.setText(commentRequestDto.getText());
            comment.setItem(item2);
            return comment;
        });

        CommentResponseDto commentResponseDto = itemService.createComment(user1.getId(), item2.getId(), commentRequestDto);
        assertEquals(commentResponseDto.getAuthorName(), user1.getName());
        assertEquals(commentResponseDto.getText(), commentRequestDto.getText());
    }

    @Test
    void deleteItemNoUserExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemService.deleteItem(user1.getId(), item2.getId()));
    }

    @Test
    void deleteItemNoItemExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(user1.getId(), item2.getId()));
    }

    @Test
    void deleteItemUserNotOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(user2.getId(), item1.getId()));
    }
}
