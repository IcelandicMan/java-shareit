package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItServer.class, UserServiceImpl.class})
@TestPropertySource(properties = {"db.name=test"})
@Transactional
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemSerVice itemSerVice;


    private UserRequestDto userRequestDto;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;

    @BeforeEach
    void beforeEach() {

        userRequestDto = new UserRequestDto();
        userRequestDto.setName("Arthur");
        userRequestDto.setEmail("arthur@gmail.com");

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setName("NameItem1");
        itemRequestDto1.setDescription("Description item1");
        itemRequestDto1.setAvailable(true);

        itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setName("UPNameItem1");
        itemRequestDto2.setDescription("UPDescription item1");
        itemRequestDto2.setAvailable(false);
    }

    @Test
    void createUserTest() {
        UserResponseDto user = userService.createUser(userRequestDto);
        itemSerVice.createItem(user.getId(), itemRequestDto1);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item queryItem = query
                .setParameter("name", itemRequestDto1.getName())
                .getSingleResult();

        assertThat(queryItem.getId(), notNullValue());
        assertEquals(itemRequestDto1.getName(), queryItem.getName());
        assertEquals(itemRequestDto1.getDescription(), queryItem.getDescription());
        assertEquals(itemRequestDto1.getAvailable(), queryItem.getAvailable());
        assertEquals(queryItem.getOwner().getName(), user.getName());
        assertEquals(queryItem.getOwner().getEmail(), user.getEmail());
    }

    @Test
    void getItemsTest() {
        UserResponseDto user = userService.createUser(userRequestDto);
        itemSerVice.createItem(user.getId(), itemRequestDto1);
        itemSerVice.createItem(user.getId(), itemRequestDto2);
        List<Item> allItems = em.createQuery("SELECT i FROM Item i", Item.class)
                .getResultList();

        assertEquals(2, allItems.size());
        assertEquals(allItems.get(0).getName(), itemRequestDto1.getName());
        assertEquals(allItems.get(0).getDescription(), itemRequestDto1.getDescription());
        assertEquals(allItems.get(1).getName(), itemRequestDto2.getName());
        assertEquals(allItems.get(1).getDescription(), itemRequestDto2.getDescription());
    }


}