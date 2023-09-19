package ru.practicum.shareit.itemRequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.itemRequest.dto.ItemRequestRequestedDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestResponsedDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemRequestRequestedDto itemRequestRequestedDto;
    private ItemRequestRequestedDto itemRequestRequestedDto2;

    private ItemRequestResponsedDto itemRequestResponsedDto;

    @BeforeEach
    void setUp() {
        itemRequestRequestedDto = new ItemRequestRequestedDto();
        itemRequestRequestedDto.setDescription("Запрос на гитару");

        itemRequestRequestedDto2 = new ItemRequestRequestedDto();
        itemRequestRequestedDto2.setDescription("Нужна плойка");

        itemRequestResponsedDto = new ItemRequestResponsedDto();
        itemRequestRequestedDto.setDescription(itemRequestRequestedDto.getDescription());
        itemRequestResponsedDto.setId(1L);
        itemRequestResponsedDto.setCreated(LocalDateTime.now());
    }

    @Test
    void createItemRequestTest() throws Exception {

        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestRequestedDto.class))).thenReturn(itemRequestResponsedDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "123")
                        .content(mapper.writeValueAsString(itemRequestRequestedDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponsedDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponsedDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).createItemRequest(123L, itemRequestRequestedDto);
    }

    @Test
    void createItemRequestWithNotValidTest() throws Exception {

        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestRequestedDto.class))).thenThrow(new RuntimeException());
        itemRequestRequestedDto.setDescription(null);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "123")
                        .content(mapper.writeValueAsString(itemRequestRequestedDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).createItemRequest(123L, itemRequestRequestedDto);
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), anyLong())).thenReturn(itemRequestResponsedDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponsedDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponsedDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).getItemRequest(1L, 1L);
    }

    @Test
    void getItemsByIdTest() throws Exception {
        when(itemRequestService.getAllItemRequestByOwner(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestResponsedDto));

        mvc.perform(get("/requests", 1)
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestResponsedDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestResponsedDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).getAllItemRequestByOwner(1L, 0, 5);
    }

    @Test
    void getAllItemTest() throws Exception {
        when(itemRequestService.getAllItemRequest(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestResponsedDto));

        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestResponsedDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestResponsedDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).getAllItemRequest(1L, 0, 5);
    }
}