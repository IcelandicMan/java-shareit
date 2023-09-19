package ru.practicum.shareit.itemRequest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemRequest.dto.ItemRequestRequestedDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponsedDto;
import ru.practicum.shareit.itemRequest.exeption.ItemRequestNotFoundException;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
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
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;
    private Item item1;

    private ItemRequestRequestedDto itemRequestRequestedDto;

    private ItemRequest itemRequest;
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

        itemRequestRequestedDto = new ItemRequestRequestedDto();
        itemRequestRequestedDto.setDescription("Первый тесовый запрос");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription(itemRequestRequestedDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());


        item1 = new Item();
        item1.setId(1L);
        item1.setName("NameItem1");
        item1.setDescription("Description item1");
        item1.setAvailable(true);
        item1.setOwner(user1);

    }

    @Test
    void createItemRequestTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestResponsedDto response = itemRequestService.createItemRequest(user1.getId(), itemRequestRequestedDto);

        assertNotNull(response);
        assertEquals(response.getId(), itemRequest.getId());
        assertEquals(response.getDescription(), itemRequest.getDescription());

        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequestWithWrongUserTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> itemRequestService.createItemRequest(1L, itemRequestRequestedDto));
    }

    @Test
    void getRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequestResponsedDto response = itemRequestService.getItemRequest(user1.getId(), itemRequest.getId());

        assertEquals(response.getId(), itemRequest.getId());
        assertEquals(response.getDescription(), itemRequest.getDescription());
    }

    @Test
    void getRequestNotExisted() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getItemRequest(user1.getId(), itemRequest.getId()));
    }

    @Test
    void getRequestsByOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(anyLong(), any(Pageable.class))).thenReturn(List.of(itemRequest));

        List<ItemRequestResponsedDto> responses = itemRequestService.getAllItemRequestByOwner(user1.getId(), 0, 5);

        assertEquals(1, responses.size());
        assertEquals(responses.get(0).getId(), itemRequest.getId());
        assertEquals(responses.get(0).getDescription(), itemRequest.getDescription());
    }

    @Test
    void getRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.findAllByCreatedDesc(anyLong(), any(Pageable.class))).thenReturn(List.of(itemRequest));

        List<ItemRequestResponsedDto> responses = itemRequestService.getAllItemRequest(user1.getId(), 0, 5);

        assertEquals(1, responses.size());
        assertEquals(responses.get(0).getId(), itemRequest.getId());
        assertEquals(responses.get(0).getDescription(), itemRequest.getDescription());
    }
}

