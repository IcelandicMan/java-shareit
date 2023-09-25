package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @MockBean
    private ItemServiceImpl itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;

    private ItemResponseDto itemResponseDto;

    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;

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

        itemResponseDto = ItemMapper.itemToItemResponseDto(user1.getId(), item1, List.of(), List.of(), List.of());

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName(item1.getName());
        itemRequestDto.setDescription(item1.getDescription());
        itemRequestDto.setAvailable(item1.getAvailable());

        commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("Comment from user 2");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item1);
        comment.setAuthor(user2);
        comment.setText(commentRequestDto.getText());

        commentResponseDto = CommentMapper.commentToCommentResponse(comment);
    }


    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemRequestDto.class))).thenReturn(itemResponseDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemResponseDto.getLastBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.nextBooking", is(itemResponseDto.getNextBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.comments", is(itemResponseDto.getComments()), List.class));

        verify(itemService, times(1)).createItem(1L, itemRequestDto);
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemRequestDto.class))).thenReturn(itemResponseDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemResponseDto.getLastBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.nextBooking", is(itemResponseDto.getNextBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.comments", is(itemResponseDto.getComments()), List.class));

        verify(itemService, times(1)).updateItem(1L, 1L, itemRequestDto);
    }


    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemResponseDto.getLastBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.nextBooking", is(itemResponseDto.getNextBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.comments", is(itemResponseDto.getComments()), List.class));

        verify(itemService, times(1)).getItem(1L, 1L);
    }


    @Test
    void getItemsByIdTest() throws Exception {
        when(itemService.getItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemResponseDto));

        mvc.perform(get("/items", 1)
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.[0].description", is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].available", is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.[0].requestId", is(itemResponseDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking", is(itemResponseDto.getLastBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.[0].nextBooking", is(itemResponseDto.getNextBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.[0].comments", is(itemResponseDto.getComments()), List.class));

        verify(itemService, times(1)).getItems(1L, 0, 5);
    }

    @Test
    void searchItemTest() throws Exception {
        when(itemService.searchItems(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(itemResponseDto));

        mvc.perform(get("/items/search", 1)
                        .param("text", "searching")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.[0].description", is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.[0].available", is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.[0].requestId", is(itemResponseDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking", is(itemResponseDto.getLastBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.[0].nextBooking", is(itemResponseDto.getNextBooking()), BookingForItemDto.class))
                .andExpect(jsonPath("$.[0].comments", is(itemResponseDto.getComments()), List.class));

        verify(itemService, times(1)).searchItems(1L, "searching", 0, 5);
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentRequestDto.class))).thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentResponseDto)));
    }
}