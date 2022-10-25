package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;
    @Mock
    private CommentService commentService;

    @InjectMocks
    private ItemController itemController;
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private User userOne;
    private User userTwo;
    private ItemRequest itemRequest;
    private Item item;
    private Comment comment;

    private ResultMatcher resultMatcher = content().json("{" +
            "\"id\": 1," +
            "\"name\": \"Item One\"," +
            "\"description\": \"Description item one\", " +
            "\"available\": true," +
            "\"lastBooking\": null, " +
            "\"nextBooking\": null, " +
            "\"comments\": []}");
    private ResultMatcher resultMatcherList = content().json("[{" +
            "\"id\": 1," +
            "\"name\": \"Item One\"," +
            "\"description\": \"Description item one\", " +
            "\"available\": true," +
            "\"lastBooking\": null, " +
            "\"nextBooking\": null, " +
            "\"comments\": []}]");

    private ResultMatcher getResultMatcherList = content().json("[{" +
            "\"id\": 1," +
            "\"name\": \"Item One\"," +
            "\"description\": \"Description item one\", " +
            "\"available\": true, " +
            "\"requestId\": 1}]");

    @BeforeEach
    void createObjects() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        mapper.registerModule(new JavaTimeModule());

        userOne = new User(
                1L,
                "NameOne@gmail.com",
                "Name One");

        userTwo = new User(
                2L,
                "NameTwo@gmail.com",
                "Name Two");

        itemRequest = new ItemRequest(
                1L,
                "Description item request one",
                userTwo,
                LocalDateTime.now());

        item = new Item(
                1L,
                "Item One",
                "Description item one",
                true,
                userOne,
                itemRequest.getId());

        comment = new Comment(
                1L,
                "Text comment one",
                item,
                userOne,
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Вызов метода getItemsTest: получение всех вещей")
    void getItemsTest() throws Exception {
        List<ItemDtoWithBooking> items = new ArrayList<>();
        ItemDtoWithBooking itemDtoWithBooking = ItemMapper.toItemDtoWithBooking(item);
        items.add(itemDtoWithBooking);
        when(itemService.getAllItems(item.getOwner().getId(), 0, 20))
                .thenReturn(items);
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", item.getOwner().getId())
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(resultMatcherList);
    }

    @Test
    @DisplayName("Вызов метода getItemByIdTest: получение вещей по id")
    void getItemByIdTest() throws Exception {
        ItemDtoWithBooking itemDtoWithBooking = ItemMapper.toItemDtoWithBooking(item);
        when(itemService.getItemById(item.getOwner().getId(), 1L))
                .thenReturn(itemDtoWithBooking);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", item.getOwner().getId()))
                .andExpect(status().isOk())
                .andExpect(resultMatcher);
    }

    @Test
    @DisplayName("Вызов метода getItemSearchTest: поиск вещей")
    void getItemSearchTest() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        items.add(itemDto);
        String text = item.getDescription().substring(0, 3);
        when(itemService.getItemSearch(text, 0, 20)).thenReturn(items);
        mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(getResultMatcherList);
    }

    @Test
    @DisplayName("Вызов метода createItemTest: создание вещи")
    void createItemTest() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.createItem(itemDto, item.getOwner().getId()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userOne.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));
    }

    @Test
    @DisplayName("Вызов метода createCommentTest: создание комментария")
    void createCommentTest() throws Exception {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        when(commentService.createComment(commentDto, item.getId(), userOne.getId()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", userOne.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }

    @Test
    @DisplayName("Вызов метода removeItemTest: удаление вещи по id")
    void removeItemTest() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .content(new ObjectMapper().writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Вызов метода patchItemTest: обновление вещи")
    void patchItemTest() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Item itemTwo = item;
        ItemDto itemDto2 = ItemMapper.toItemDto(itemTwo);
        itemDto2.setName("Item Two");
        itemService.createItem(itemDto, item.getOwner().getId());
        when(itemService.patchItem(itemDto2, item.getOwner().getId(), itemDto.getId())).thenReturn(itemDto2);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", item.getOwner().getId())
                        .content(mapper.writeValueAsString(itemDto2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"id\": 1," +
                        "\"name\": \"Item Two\"," +
                        "\"description\": \"Description item one\", " +
                        "\"available\": true, " +
                        "\"requestId\": 1}"));
    }
}