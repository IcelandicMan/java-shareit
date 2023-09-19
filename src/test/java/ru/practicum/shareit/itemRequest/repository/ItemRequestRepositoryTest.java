package ru.practicum.shareit.itemRequest.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;


    private User user;
    private User user2;
    private User user3;

    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;

    private ItemRequest itemRequest4;
    private Item item;

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

        user3 = new User();
        user3.setName("Test");
        user3.setEmail("test@gmail.com");

        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    @Test
    void findAllByUserIdOrderByCreatedDescThenFindAllFromNonOwnerUser() {
        itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужен ноутбук");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.of(2023, 12, 10, 9, 0));
        itemRequestRepository.save(itemRequest);

        itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("Требуются лыжи");
        itemRequest2.setRequestor(user);
        itemRequest2.setCreated(LocalDateTime.of(2023, 12, 12, 9, 0));
        itemRequestRepository.save(itemRequest2);

        itemRequest3 = new ItemRequest();
        itemRequest3.setDescription("Сковородка");
        itemRequest3.setRequestor(user);
        itemRequest3.setCreated(LocalDateTime.of(2023, 12, 11, 9, 0));
        itemRequestRepository.save(itemRequest3);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(1L, pageRequest);
        assertEquals(3, itemRequests.size());
        assertEquals("Нужен ноутбук", itemRequests.get(2).getDescription());
        assertEquals("Требуются лыжи", itemRequests.get(0).getDescription());
        assertEquals("Сковородка", itemRequests.get(1).getDescription());

        itemRequest4 = new ItemRequest();
        itemRequest4.setDescription("Дрель");
        itemRequest4.setRequestor(user2);
        itemRequest4.setCreated(LocalDateTime.of(2023, 10, 23, 9, 0));
        itemRequestRepository.save(itemRequest4);

        itemRequests = itemRequestRepository.findAllByCreatedDesc(2L, pageRequest);
        assertEquals(3, itemRequests.size());
        assertEquals("Нужен ноутбук", itemRequests.get(2).getDescription());
        assertEquals("Требуются лыжи", itemRequests.get(0).getDescription());
        assertEquals("Сковородка", itemRequests.get(1).getDescription());

        itemRequests = itemRequestRepository.findAllByCreatedDesc(3L, pageRequest);
        assertEquals(4, itemRequests.size());
        assertEquals("Нужен ноутбук", itemRequests.get(2).getDescription());
        assertEquals("Требуются лыжи", itemRequests.get(0).getDescription());
        assertEquals("Сковородка", itemRequests.get(1).getDescription());
        assertEquals("Дрель", itemRequests.get(3).getDescription());
    }

}