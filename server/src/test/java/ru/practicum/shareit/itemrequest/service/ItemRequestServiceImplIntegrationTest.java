package ru.practicum.shareit.itemrequest.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemSerVice;
import ru.practicum.shareit.itemrequest.dto.ItemRequestRequestedDto;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItServer.class, UserServiceImpl.class})
@TestPropertySource(properties = {"db.name=test"})
@Transactional
class ItemRequestServiceImplIntegrationTest {


    private final EntityManager em;
    private final UserService userService;
    private final ItemSerVice itemSerVice;
    private final ItemRequestService itemRequestService;

    private UserRequestDto userRequestDto;
    private UserRequestDto userRequestDto2;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestRequestedDto itemRequestRequestedDto;
    private ItemRequestRequestedDto itemRequestRequestedDto2;


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
    void createItemRequest() {

        UserResponseDto user1 = userService.createUser(userRequestDto);
        UserResponseDto user2 = userService.createUser(userRequestDto2);
        ItemResponseDto item = itemSerVice.createItem(user1.getId(), itemRequestDto1);


        itemRequestRequestedDto = new ItemRequestRequestedDto();
        itemRequestRequestedDto.setDescription("Запрос 1");

        Long id = itemRequestService.createItemRequest(user2.getId(), itemRequestRequestedDto).getId();

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest queryItemRequest = query
                .setParameter("id", id)
                .getSingleResult();
        assertNotNull(queryItemRequest);
        assertEquals(queryItemRequest.getDescription(), itemRequestRequestedDto.getDescription());
        assertEquals(queryItemRequest.getRequestor().getName(), user2.getName());
    }

    @Test
    void getItemsTest() {
        UserResponseDto user1 = userService.createUser(userRequestDto);
        UserResponseDto user2 = userService.createUser(userRequestDto2);
        ItemResponseDto item = itemSerVice.createItem(user1.getId(), itemRequestDto1);

        itemRequestRequestedDto = new ItemRequestRequestedDto();
        itemRequestRequestedDto.setDescription("Запрос 1");

        itemRequestRequestedDto2 = new ItemRequestRequestedDto();
        itemRequestRequestedDto2.setDescription("Запрос 2");

        itemRequestService.createItemRequest(user2.getId(), itemRequestRequestedDto);
        itemRequestService.createItemRequest(user2.getId(), itemRequestRequestedDto2);
        List<ItemRequest> allIItemRequest = em.createQuery("SELECT ir FROM ItemRequest ir", ItemRequest.class)
                .getResultList();

        assertEquals(2, allIItemRequest.size());
    }


}