package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.PageRequestOverride;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    private ItemService itemService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private ItemRequestRepository itemRequestRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    private User userOne;
    private User userTwo;
    private ItemRequest itemRequestOne;
    private ItemRequest itemRequestTwo;
    private Item itemOne;
    private Item itemTwo;
    private Comment comment;
    private PageRequestOverride pageRequest;

    @BeforeEach
    void createObjects() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);

        when(userRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));
        when(itemRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));
        when(itemRequestRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));

        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);

        userOne = new User(
                1L,
                "nameOne@gmail.com",
                "Name One");
        userTwo = new User(
                2L,
                "nameTwo@gmail.com",
                "Name Two");

        itemRequestOne = new ItemRequest(
                1L,
                "Description item request one",
                userTwo,
                LocalDateTime.now());
        itemRequestTwo = new ItemRequest(
                2L,
                "Description item request two",
                userTwo,
                LocalDateTime.now());

        itemOne = new Item(
                1L,
                "Item One",
                "Description item one",
                true,
                userOne,
                itemRequestOne.getId());
        itemTwo = new Item(
                2L,
                "Item Two",
                "Description item two",
                true,
                userOne,
                itemRequestTwo.getId());

        comment = new Comment(
                1L,
                "Text comment one",
                itemOne,
                userTwo,
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Вызов метода getAllItemsWithInvalidFromTest: получение всех вещей по не верному from")
    void getAllItemsWithInvalidFromTest() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.getAllItems(userOne.getId(), -1, 20));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllItemsWithInvalidSizeTest: получение всех вещей не верному size")
    void getAllItemsWithInvalidSizeTest() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.getAllItems(userOne.getId(), 0, 0));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllItemsWithInvalidFromAndSizeTest: получение всех вещей не верному from и size")
    void getAllItemsWithInvalidFromAndSizeTest() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.getAllItems(userOne.getId(), -1, 0));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }


    @Test
    @DisplayName("Вызов метода getItemByIdTest: получение вещи по id")
    void getItemByIdTest() {
        when(itemRepository.findById(itemOne.getId()))
                .thenReturn(Optional.of(itemOne));
        when(commentRepository.findAllByItemId(itemOne.getId()))
                .thenReturn(List.of(comment));

        var itemDtoWithBooking = itemService.getItemById(itemOne.getOwner().getId(), itemOne.getId());

        assertNotNull(itemDtoWithBooking);
        assertEquals(itemOne.getId(), itemDtoWithBooking.getId());
        assertEquals(itemOne.getDescription(), itemDtoWithBooking.getDescription());
        assertEquals(itemOne.getName(), itemDtoWithBooking.getName());
        assertEquals(itemOne.getAvailable(), itemDtoWithBooking.getAvailable());
    }

    @Test
    @DisplayName("Вызов метода getItemByIdNotExistCommentTest: получение вещи по id без комментария")
    void getItemByIdWithoutCommentTest() {
        when(itemRepository.findById(itemTwo.getId()))
                .thenReturn(Optional.of(itemTwo));

        when(commentRepository.findAllByItemId(itemTwo.getId()))
                .thenReturn(List.of());

        var itemDtoWithBooking = itemService.getItemById(itemTwo.getOwner().getId(), itemTwo.getId());

        assertNotNull(itemDtoWithBooking);
        assertEquals(itemTwo.getId(), itemDtoWithBooking.getId());
        assertEquals(itemTwo.getDescription(), itemDtoWithBooking.getDescription());
        assertEquals(itemTwo.getName(), itemDtoWithBooking.getName());
        assertEquals(itemTwo.getAvailable(), itemDtoWithBooking.getAvailable());
    }

    @Test
    @DisplayName("Вызов метода getItemWithInvalidIdTest: получение вещи по не верному id")
    void getItemWithInvalidIdTest() {
        long id = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(1L, id));

        assertEquals("Вещь 50 не существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getItemSearchTest: поиск вещи")
    void getItemSearchTest() {
        pageRequest = PageRequestOverride.of(0, 20);

        List<Item> items = new ArrayList<>();
        items.add(itemOne);
        items.add(itemTwo);
        String text = itemOne.getDescription().substring(0, 3);
        when(itemRepository.search(text, pageRequest))
                .thenReturn(items);
        List<ItemDto> itemDtoList = itemService.getItemSearch(text, 0, 20);
        assertNotNull(itemDtoList);
        assertEquals(items.size(), itemDtoList.size());
        assertEquals(itemOne.getName(), itemDtoList.get(0).getName());
    }

    @Test
    @DisplayName("Вызов метода getItemSearchWithInvalidFromTest: поиск вещи по не верному from")
    void getItemSearchWithInvalidFromTest() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.getItemSearch(itemOne.getDescription(), -1, 20));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getItemSearchWithInvalidSizeTest: поиск вещи по не верному size")
    void getItemSearchWithInvalidSizeTest() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.getItemSearch(itemOne.getDescription(), 0, 0));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getItemSearchWithInvalidSizeTest: поиск вещи по не верному from и size")
    void getItemSearchWithInvalidFromAndSizeTest() {

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.getItemSearch(itemOne.getDescription(), -1, 0));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getItemSearchWithEmptyTextTest: поиск вещи по пустому text")
    void getItemSearchWithEmptyTextTest() {
        Item item = new Item(
                1L,
                "Item One",
                "",
                true,
                userOne,
                itemRequestOne.getId());

        pageRequest = PageRequestOverride.of(0, 20);

        List<Item> items = new ArrayList<>();
        String text = item.getDescription();
        when(itemRepository.search(text, pageRequest))
                .thenReturn(items);
        List<ItemDto> itemDtoList = itemService.getItemSearch(text, 0, 20);

        assertNotNull(itemDtoList);
        assertEquals(items.size(), itemDtoList.size());
    }

    @Test
    @DisplayName("Вызов метода createItemTest: создание вещи")
    void createItemTest() {
        when(itemRepository.save(itemOne))
                .thenReturn(itemOne);
        when(userRepository.findById(itemOne.getOwner().getId()))
                .thenReturn(Optional.of(itemOne.getOwner()));

        var itemDto = itemService.createItem(ItemMapper.toItemDto(itemOne), 1L);

        assertNotNull(itemDto);
        assertEquals(itemOne.getId(), itemDto.getId());
        assertEquals(itemOne.getName(), itemDto.getName());
        assertEquals(itemOne.getDescription(), itemDto.getDescription());
    }

    @Test
    @DisplayName("Вызов метода createItemWithEmptyNameTest: создание вещи с пустым name")
    void createItemWithEmptyNameTest() {
        Item item = new Item(
                3L,
                "",
                "Description item",
                true,
                userOne,
                itemRequestOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(ItemMapper.toItemDto(item), userOne.getId()));

        assertEquals("Данное поле не может быть пустым.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createItemWithEmptyDescriptionTest: создание вещи с пустым description")
    void createItemWithEmptyDescriptionTest() {
        Item item = new Item(
                3L,
                "Name",
                null,
                true,
                userOne,
                itemRequestOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(ItemMapper.toItemDto(item), userOne.getId()));

        assertEquals("Данное поле не может быть пустым.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createItemWithEmptyAvailableTest: создание вещи с пустым available")
    void createItemWithEmptyAvailableTest() {
        Item item = new Item(
                3L,
                "Name",
                "Description item",
                null,
                userOne,
                itemRequestOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(ItemMapper.toItemDto(item), userOne.getId()));

        assertEquals("Данное поле не может быть пустым.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createItemNotExistUserTest: создание вещи с не существующим пользователем")
    void createItemNotExistUserTest() {
        long userId = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(ItemMapper.toItemDto(itemOne), userId));

        assertEquals("Пользователь 50 не существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода removeItemByIdTest: удаление вещи по id")
    void removeItemByIdTest() {
        itemService.removeItemById(itemTwo.getId());

        assertNotNull(itemTwo);
    }

    @Test
    @DisplayName("Вызов метода updateItemTest: обновление имени, описания и статуса вещи")
    void updateItemTest() {
        Item item = new Item();
        item.setName("name name");
        item.setDescription("Name description");
        item.setAvailable(false);

        when(itemRepository.findById(itemOne.getId()))
                .thenReturn(Optional.of(itemOne));
        itemOne.setName(item.getName());
        itemOne.setDescription(item.getDescription());
        itemOne.setAvailable(item.getAvailable());

        var itemResult = itemService.patchItem(ItemMapper.toItemDto(item), userOne.getId(), itemOne.getId());

        assertNotNull(itemResult);
        assertEquals(itemOne.getDescription(), itemResult.getDescription());
        assertEquals(itemOne.getName(), itemResult.getName());
        assertEquals(itemOne.getAvailable(), itemResult.getAvailable());
    }

    @Test
    @DisplayName("Вызов метода updateItemTest: обновление имени вещи")
    void updateItemNameTest() {
        Item item = new Item();
        item.setName("name name");

        when(itemRepository.findById(itemOne.getId()))
                .thenReturn(Optional.of(itemOne));
        itemOne.setName(item.getName());

        var itemResult = itemService.patchItem(ItemMapper.toItemDto(item), userOne.getId(), itemOne.getId());

        assertNotNull(itemResult);
        assertEquals(itemOne.getDescription(), itemResult.getDescription());
        assertEquals(itemOne.getName(), itemResult.getName());
        assertEquals(itemOne.getAvailable(), itemResult.getAvailable());
    }

    @Test
    @DisplayName("Вызов метода updateItemDescriptionTest: обновление описания вещи")
    void updateItemDescriptionTest() {
        Item item = new Item();
        item.setDescription("Name description");

        when(itemRepository.findById(itemOne.getId()))
                .thenReturn(Optional.of(itemOne));
        itemOne.setDescription(item.getDescription());

        var itemResult = itemService.patchItem(ItemMapper.toItemDto(item), userOne.getId(), itemOne.getId());

        assertNotNull(itemResult);
        assertEquals(itemOne.getDescription(), itemResult.getDescription());
        assertEquals(itemOne.getName(), itemResult.getName());
        assertEquals(itemOne.getAvailable(), itemResult.getAvailable());
    }

    @Test
    @DisplayName("Вызов метода updateItemAvailableTest: обновление статуса вещи")
    void updateItemAvailableTest() {
        Item item = new Item();
        item.setAvailable(false);

        when(itemRepository.findById(itemOne.getId()))
                .thenReturn(Optional.of(itemOne));
        itemOne.setAvailable(item.getAvailable());

        var itemResult = itemService.patchItem(ItemMapper.toItemDto(item), userOne.getId(), itemOne.getId());

        assertNotNull(itemResult);
        assertEquals(itemOne.getDescription(), itemResult.getDescription());
        assertEquals(itemOne.getName(), itemResult.getName());
        assertEquals(itemOne.getAvailable(), itemResult.getAvailable());
    }

    @Test
    @DisplayName("Вызов метода updateItemNotExistUserTest: обновление вещи с не существующим пользователем")
    void updateItemNotExistUserTest() {
        Item item = new Item();
        item.setName("name name");
        item.setDescription("Name description");

        when(itemRepository.findById(itemOne.getId()))
                .thenReturn(Optional.of(itemOne));

        long userId = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.patchItem(ItemMapper.toItemDto(item), userId, itemOne.getId()));

        assertEquals("Пользователь 50 не владеет вещью.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода updateItemWithInvalidIdTest: обновление вещи по не верному id")
    void updateItemWithInvalidIdTest() {
        Item item = new Item();

        long id = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.patchItem(ItemMapper.toItemDto(item), userOne.getId(), id));

        assertEquals("Вещь 50 не существует.", exception.getMessage());
    }
}