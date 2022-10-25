package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateEnum;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommentServiceImplTest {

    private CommentService commentService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    private User userOne;
    private User userTwo;
    private ItemRequest itemRequestOne;
    private Item itemOne;
    private Comment comment;
    private Booking booking;


    @BeforeEach
    void createObjects() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);

        when(userRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));
        when(itemRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));

        commentService = new CommentServiceImpl(commentRepository, itemRepository, userRepository, bookingRepository);

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

        itemOne = new Item(
                1L,
                "Item One",
                "Description item one",
                true,
                userOne,
                itemRequestOne.getId());

        comment = new Comment(
                1L,
                "Text comment one",
                itemOne,
                userTwo,
                LocalDateTime.now());

        booking = new Booking(
                1L, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(2),
                itemOne,
                userTwo,
                BookingStateEnum.APPROVED);
    }

    @Test
    @DisplayName("Вызов метода createCommentTest: создание комментария")
    void createCommentTest() {
        when(itemRepository.findById(itemOne.getId()))
                .thenReturn(Optional.of(itemOne));
        when(userRepository.findById(userTwo.getId()))
                .thenReturn(Optional.of(userTwo));

        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);

        when(bookingRepository
                .searchBookingByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(bookingsList);

        when(commentRepository.save(comment))
                .thenReturn(comment);

        var commentDto = commentService.createComment(CommentMapper.toCommentDto(comment),
                itemOne.getId(), userTwo.getId());
        assertNotNull(commentDto);
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(userTwo.getName(), commentDto.getAuthorName());
        assertEquals(comment.getId(), commentDto.getId());
    }

    @Test
    @DisplayName("Вызов метода createCommentEmptyTest: создание пустого комментария")
    void createCommentEmptyTest() {
        Comment commentEmpty = new Comment(
                1L,
                "",
                itemOne,
                userTwo,
                LocalDateTime.now());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> commentService.createComment(
                        CommentMapper.toCommentDto(commentEmpty),
                        itemOne.getId(),
                        userTwo.getId()));

        assertEquals("Комментарий не может быть пустым.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createCommentNotBookingTest: создание комментария без аренды")
    void createCommentNotBookingTest() {

        when(commentRepository.save(comment))
                .thenReturn(comment);

        final BookingException exception = assertThrows(
                BookingException.class,
                () -> commentService.createComment(
                        CommentMapper.toCommentDto(comment),
                        itemOne.getId(),
                        userTwo.getId()));

        assertEquals("Пользователь 2 не брал в аренду вещь 1.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createCommentNotExistUserTest: создание комментария несуществующим пользователем")
    void createCommentNotExistUserTest() {
        when(itemRepository.findById(itemOne.getId()))
                .thenReturn(Optional.of(itemOne));
        when(userRepository.findById(userTwo.getId()))
                .thenReturn(Optional.of(userTwo));

        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);

        when(bookingRepository
                .searchBookingByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(bookingsList);

        when(commentRepository.save(comment))
                .thenReturn(comment);

        long userId = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> commentService.createComment(
                        CommentMapper.toCommentDto(comment),
                        itemOne.getId(),
                        userId));

        assertEquals("Пользователь 50 не существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createCommentNotExistItemTest: создание комментария несуществующей вещи")
    void createCommentNotExistItemTest() {
        when(itemRepository.findById(itemOne.getId()))
                .thenReturn(Optional.of(itemOne));
        when(userRepository.findById(userTwo.getId()))
                .thenReturn(Optional.of(userTwo));

        List<Booking> bookingsList = new ArrayList<>();
        bookingsList.add(booking);

        when(bookingRepository
                .searchBookingByBookerIdAndItemIdAndEndIsBefore(anyLong(), anyLong(), any()))
                .thenReturn(bookingsList);

        when(commentRepository.save(comment))
                .thenReturn(comment);

        long itemId = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> commentService.createComment(
                        CommentMapper.toCommentDto(comment),
                        itemId,
                        userTwo.getId()));

        assertEquals("Вещь 50 не существует.", exception.getMessage());
    }
}