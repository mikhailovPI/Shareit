package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private ItemRequest itemRequestOne;
    private User userOne;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
        mapper.registerModule(new JavaTimeModule());

        userOne = new User(
                1L,
                "nameOne@gmail.com",
                "Name One");

        itemRequestOne = new ItemRequest(
                1L,
                "Description item request one",
                userOne,
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Вызов метода getAllItemRequestTest: получение всех запросов")
    void getAllItemRequestTest() throws Exception {
        List<ItemRequestWithItemDto> result = new ArrayList<>();
        ItemRequestWithItemDto itemRequestDtoWithItems = ItemRequestMapper.toItemRequestWithItemDto(itemRequestOne);
        result.add(itemRequestDtoWithItems);

        when(itemRequestService.getAllItemRequest(userOne.getId()))
                .thenReturn(result);
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userOne.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                        "\"id\": 1," +
                        " \"description\": \"Description item request one\"}]"));
    }

    @Test
    @DisplayName("Вызов метода getItemRequestByIdTest: получение запроса по id")
    void getItemRequestByIdTest() throws Exception {
        ItemRequestWithItemDto itemRequestDtoWithItems = ItemRequestMapper.toItemRequestWithItemDto(itemRequestOne);
        when(itemRequestService.getItemRequestById(userOne.getId(), itemRequestOne.getId()))
                .thenReturn(itemRequestDtoWithItems);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", userOne.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"id\": 1," +
                        " \"description\": \"Description item request one\"}"));
    }

    @Test
    @DisplayName("Вызов метода getItemRequestOtherUsersTest: получение запросов другими пользователями")
    void getItemRequestOtherUsersTest() throws Exception {
        List<ItemRequestWithItemDto> result = new ArrayList<>();
        ItemRequestWithItemDto itemRequestDtoWithItems = ItemRequestMapper.toItemRequestWithItemDto(itemRequestOne);
        result.add(itemRequestDtoWithItems);
        when(itemRequestService.getItemRequestOtherUsers(userOne.getId(),0, 20))
                .thenReturn(result);
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userOne.getId())
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                        "\"id\": 1," +
                        " \"description\": \"Description item request one\"}]"));
    }

    @Test
    @DisplayName("Вызов метода createItemRequestTest: создание запроса на вещь")
    void createItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any()))
                .thenReturn(ItemRequestMapper.toItemRequestDto(itemRequestOne));
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(ItemRequestMapper.toItemRequestDto(itemRequestOne)))
                        .header("X-Sharer-User-Id", userOne.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestOne.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestOne.getDescription()));
    }
}