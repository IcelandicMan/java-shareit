package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;


    private User user;
    private User user2;
    private Item item;
    private Item item2;
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
    }

    @Test
    public void searchingItemAndFindByOwnerTest() {

        item = new Item();
        item.setName("Укулеле");
        item.setDescription("струны карбоновые");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        List<Item> items = itemRepository.findAllBySearching("карбон", pageRequest);
        assertEquals(1, items.size());
        assertEquals("струны карбоновые", items.get(0).getDescription());

        item2 = new Item();
        item2.setName("Фотоаппарат");
        item2.setDescription("Canon");
        item2.setOwner(user2);
        item2.setAvailable(true);
        itemRepository.save(item2);

        items = itemRepository.findAllByOwnerId(2L, pageRequest);
        assertEquals(1, items.size());
        assertEquals("Canon", items.get(0).getDescription());
    }
}