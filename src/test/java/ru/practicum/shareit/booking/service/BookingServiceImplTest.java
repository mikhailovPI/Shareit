package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.PageRequestOverride;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateEnum;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private Booking booking;
    private User owner;
    private User booker;
    private Item item;
    private PageRequestOverride pageRequest;


    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

        owner = new User(
                1L,
                "NameOne@gmail.com",
                "Name One");
        booker = new User(
                2L,
                "NameTwo@gmail.com",
                "Name Two");
        item = new Item(
                1L,
                "Item One",
                "Description item one",
                true,
                owner,
                null);
        booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                booker,
                BookingStateEnum.APPROVED);

        pageRequest = PageRequestOverride.of(0, 20);
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsTest: получение всех бронирований")
    void getAllBookingsTest() {
        when(userRepository.findById(booking.getBooker().getId()))
                .thenReturn(Optional.of(booking.getBooker()));
        when(bookingRepository.findByBookerIdOrderByStartDesc(booking.getBooker().getId(), pageRequest))
                .thenReturn(Collections.singletonList(booking));
        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookings(
                        booking.getBooker().getId(),
                        "ALL",
                        0,
                        20);
        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(booking.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsStatusCurrentTest: получение всех бронирований со статусом CURRENT")
    void getAllBookingsStatusCurrentTest() {
        Booking bookingState = new Booking(
                2L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(5),
                item,
                booker,
                BookingStateEnum.CURRENT);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findCurrentBookingsByBookerIdOrderByStartDesc(
                anyLong(),
                any(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));
        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookings(
                        bookingState.getBooker().getId(),
                        "CURRENT",
                        0,
                        20);
        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsStatusPastTest: получение всех бронирований со статусом FUTURE")
    void getAllBookingsStatusFutureTest() {
        Booking bookingState = new Booking(
                2L,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(10),
                item,
                booker,
                BookingStateEnum.FUTURE);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                anyLong(),
                any(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));
        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookings(
                        bookingState.getBooker().getId(),
                        "FUTURE",
                        0,
                        20);
        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsStatusWaitingTest: получение всех бронирований со статусом WAITING")
    void getAllBookingsStatusWaitingTest() {
        Booking bookingState = new Booking(
                2L,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(10),
                item,
                booker,
                BookingStateEnum.WAITING);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findBookingsByBookerIdAndStatusOrderByStartDesc(
                anyLong(),
                any(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));
        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookings(
                        bookingState.getBooker().getId(),
                        "WAITING",
                        0,
                        20);
        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsStatusRejectedTest: получение всех бронирований со статусом REJECTED")
    void getAllBookingsStatusRejectedTest() {
        Booking bookingState = new Booking(
                2L,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(10),
                item,
                booker,
                BookingStateEnum.REJECTED);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findBookingsByBookerIdAndStatusOrderByStartDesc(
                anyLong(),
                any(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));
        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookings(
                        bookingState.getBooker().getId(),
                        "REJECTED",
                        0,
                        20);
        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsStatusFutureTest: получение всех бронирований со статусом PAST")
    void getAllBookingsStatusPastTest() {
        Booking bookingState = new Booking(
                2L,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                item,
                booker,
                BookingStateEnum.PAST);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findBookingsByBookerIdAndEndIsBeforeOrderByStartDesc(
                anyLong(),
                any(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));
        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookings(
                        bookingState.getBooker().getId(),
                        "PAST",
                        0,
                        20);
        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsWithInvalidIdUserTest:" +
            " получение бронирования по не верному id пользователя")
    void getAllBookingsWithInvalidIdUserTest() {
        long id = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookings(
                        id,
                        "ALL",
                        0,
                        20));

        assertEquals("Пользователь 50 не существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsWithInvalidStatusTest:" +
            " получение бронирования с неверным статусом")
    void getAllBookingsWithInvalidStatusTest() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getAllBookings(
                        booking.getBooker().getId(),
                        "UNSUPPORTED_STATUS",
                        0,
                        20));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsWithInvalidFromTest: получение запросов по не верному from")
    void getAllBookingsWithInvalidFromTest() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookings(
                        booking.getBooker().getId(),
                        "ALL",
                        -1,
                        20));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsWithInvalidSizeTest: получение запросов не верному size")
    void getAllBookingsWithInvalidSizeTest() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookings(
                        booking.getBooker().getId(),
                        "ALL",
                        0,
                        0));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsWithInvalidFromAndSizeTest: получение запросов не верному from и size")
    void getAllBookingsWithInvalidFromAndSizeTest() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookings(
                        booking.getBooker().getId(),
                        "ALL",
                        -1,
                        0));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getBookingByIdTest: получение бронирования по id")
    void getBookingByIdTest() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBookingById(booking.getBooker().getId(), booking.getId());
        assertNotNull(bookingDto);
        assertEquals(booking.getItem().getName(), bookingDto.getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getBookingByIdWithInvalidIdTest:" +
            " получение бронирования по не верному id")
    void getBookingByIdWithInvalidIdTest() {
        long id = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(
                        booking.getBooker().getId(),
                        id));

        assertEquals("Бронирование 50 не существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getBookingByIdWithInvalidBookingUserTest:" +
            " получение бронирования по id по неверному пользователю")
    void getBookingByIdWithInvalidBookingUserTest() {
        long id = 50;

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingById(
                        id,
                        booking.getId()));

        assertEquals("Пользователь 50 не осуществлял бронирование.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserTest: получение всех бронирований пользователя")
    void getAllBookingItemsUserTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        when(userRepository.findById(booking.getItem().getOwner().getId()))
                .thenReturn(Optional.of(booking.getBooker()));
        when(bookingRepository.getBookingsByItemOwnerId(
                booking.getItem().getOwner().getId(),
                pageRequest))
                .thenReturn(Collections.singletonList(booking));

        List<BookingDto> bookings = bookingService
                .getAllBookingItemsUser(
                        booking.getItem().getOwner().getId(),
                        "ALL",
                        0,
                        20);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(bookingDto, bookings.get(0));
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsStatusCurrentTest: " +
            "получение всех бронирований пользователя со статусом CURRENT")
    void getAllBookingItemsUserStatusCurrentTest() {
        Booking bookingState = new Booking(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(5),
                item,
                booker,
                BookingStateEnum.CURRENT);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.getBookingsByItemOwnerId(
                bookingState.getItem().getOwner().getId(),
                pageRequest))
                .thenReturn(Collections.singletonList(bookingState));

        when(bookingRepository.findCurrentBookingsByItemOwnerIdOrderByStartDesc(
                anyLong(),
                any(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));

        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookingItemsUser(
                        owner.getId(),
                        "CURRENT",
                        0,
                        20);

        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserStatusPastTest: " +
            "получение всех бронирований пользователя со статусом PAST")
    void getAllBookingItemsUserStatusPastTest() {
        Booking bookingState = new Booking(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(5),
                item,
                booker,
                BookingStateEnum.PAST);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.getBookingsByItemOwnerId(
                bookingState.getItem().getOwner().getId(),
                pageRequest))
                .thenReturn(Collections.singletonList(bookingState));

        when(bookingRepository.findBookingsByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                anyLong(),
                any(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));

        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookingItemsUser(
                        owner.getId(),
                        "PAST",
                        0,
                        20);

        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserStatusPastTest: " +
            "получение всех бронирований пользователя со статусом FUTURE")
    void getAllBookingItemsUserStatusFutureTest() {
        Booking bookingState = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                booker,
                BookingStateEnum.FUTURE);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.getBookingsByItemOwnerId(
                bookingState.getItem().getOwner().getId(),
                pageRequest))
                .thenReturn(Collections.singletonList(bookingState));

        when(bookingRepository.searchBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                anyLong(),
                any(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));

        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookingItemsUser(
                        owner.getId(),
                        "FUTURE",
                        0,
                        20);

        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserStatusWaitingTest: " +
            "получение всех бронирований пользователя со статусом WAITING")
    void getAllBookingItemsUserStatusWaitingTest() {
        Booking bookingState = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                booker,
                BookingStateEnum.WAITING);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.getBookingsByItemOwnerId(
                bookingState.getItem().getOwner().getId(),
                pageRequest))
                .thenReturn(Collections.singletonList(bookingState));

        when(bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(
                anyLong(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));

        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookingItemsUser(
                        owner.getId(),
                        "WAITING",
                        0,
                        20);

        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserStatusRejectedTest: " +
            "получение всех бронирований пользователя со статусом REJECTED")
    void getAllBookingItemsUserStatusRejectedTest() {
        Booking bookingState = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                booker,
                BookingStateEnum.REJECTED);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(bookingRepository.getBookingsByItemOwnerId(
                bookingState.getItem().getOwner().getId(),
                pageRequest))
                .thenReturn(Collections.singletonList(bookingState));

        when(bookingRepository.findBookingsByItemOwnerIdOrderByStartDesc(
                anyLong(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));

        final List<BookingDto> bookingDtoList = bookingService
                .getAllBookingItemsUser(
                        owner.getId(),
                        "REJECTED",
                        0,
                        20);

        assertNotNull(bookingDtoList);
        assertEquals(1, bookingDtoList.size());
        assertEquals(bookingState.getItem().getName(), bookingDtoList.get(0).getItem().getName());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserWithoutItemsTest:" +
            " получение всех бронирований у пользователя без вещей")
    void getAllBookingItemsUserWithoutItemsTest() {
        Booking bookingState = new Booking(
                2L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(5),
                item,
                booker,
                BookingStateEnum.CURRENT);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));

        when(bookingRepository.findCurrentBookingsByItemOwnerIdOrderByStartDesc(
                anyLong(),
                any(),
                any()))
                .thenReturn(Collections.singletonList(bookingState));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService
                        .getAllBookingItemsUser(
                                booking.getItem().getOwner().getId(),
                                "ALL",
                                0,
                                20));

        assertEquals("У пользователя нет вещей.", exception.getMessage());
    }


    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserWithInvalidStatusTest:" +
            " получение бронирования с неверным статусом")
    void getAllBookingItemsUserWithInvalidStatusTest() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getAllBookingItemsUser(
                        booking.getBooker().getId(),
                        "UNSUPPORTED_STATUS",
                        0,
                        20));

        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserWithInvalidFromTest: получение запросов по не верному from")
    void getAllBookingItemsUserWithInvalidFromTest() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingItemsUser(
                        booking.getBooker().getId(),
                        "ALL",
                        -1,
                        20));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserWithInvalidSizeTest: получение запросов не верному size")
    void getAllBookingItemsUserWithInvalidSizeTest() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingItemsUser(
                        booking.getBooker().getId(),
                        "ALL",
                        0,
                        0));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserWithInvalidFromAndSizeTest:" +
            " получение запросов не верному from и size")
    void getAllBookingItemsUserWithInvalidFromAndSizeTest() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingItemsUser(
                        booking.getBooker().getId(),
                        "ALL",
                        -1,
                        0));

        assertEquals("Переданы некорректные значения from и/или size", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserWithInvalidBookingUserTest:" +
            " получение бронирования по id несуществуюшего пользователю")
    void getAllBookingItemsUserWithInvalidBookingUserTest() {
        long id = 50;

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllBookingItemsUser(
                        id,
                        "ALL",
                        0,
                        20));

        assertEquals("Пользователь 50 не существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createBookingWithInvalidUserTest:" +
            " создание бронирования по id не существующего пользователя")
    void createBookingWithInvalidUserTest() {
        long id = 50;

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(
                        id,
                        BookingMapper.toBookingCreateDto(booking)));

        assertEquals("Пользователь 50 не существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createBookingWithInvalidItemTest:" +
            " создание бронирования по id не существующей вещи")
    void createBookingWithInvalidItemTest() {
        Item itemCreate = new Item(
                50L,
                "Item One",
                "Description item one",
                true,
                owner,
                null);

        Booking bookingCreate = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                itemCreate,
                booker,
                BookingStateEnum.CURRENT);

        when(userRepository.findById(bookingCreate.getBooker().getId()))
                .thenReturn(Optional.of(bookingCreate.getBooker()));

        when(bookingRepository.save(bookingCreate))
                .thenReturn(bookingCreate);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(
                        bookingCreate.getBooker().getId(),
                        BookingMapper.toBookingCreateDto(bookingCreate)));

        assertEquals("Вещь 50 не существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createBookingWithInvalidItemTest:" +
            " создание бронирования владельцем вещи")
    void createBookingWithInvalidOwnerTest() {
        Booking bookingCreate = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                owner,
                BookingStateEnum.CURRENT);

        when(userRepository.findById(bookingCreate.getBooker().getId()))
                .thenReturn(Optional.of(bookingCreate.getBooker()));
        when(itemRepository.findById(bookingCreate.getItem().getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.save(bookingCreate))
                .thenReturn(bookingCreate);

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(
                        bookingCreate.getBooker().getId(),
                        BookingMapper.toBookingCreateDto(bookingCreate)));

        assertEquals("Владелец вещи не может забронировать свою вещь.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createBookingWithInvalidStartTimeTest:" +
            " создание бронирования с неверным временем начала")
    void createBookingWithInvalidStartTimeTest() {
        Booking bookingCreate = new Booking(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                booker,
                BookingStateEnum.CURRENT);

        when(userRepository.findById(bookingCreate.getBooker().getId()))
                .thenReturn(Optional.of(bookingCreate.getBooker()));
        when(itemRepository.findById(bookingCreate.getItem().getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.save(bookingCreate))
                .thenReturn(bookingCreate);

        final BookingException exception = assertThrows(
                BookingException.class,
                () -> bookingService.createBooking(
                        bookingCreate.getBooker().getId(),
                        BookingMapper.toBookingCreateDto(bookingCreate)));

        assertEquals("Некорректное время начала бронирования.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createBookingWithInvalidEndTimeTest:" +
            " создание бронирования с неверным временем окончания")
    void createBookingWithInvalidEndTimeTest() {
        Booking bookingCreate = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(5),
                item,
                booker,
                BookingStateEnum.CURRENT);

        when(userRepository.findById(bookingCreate.getBooker().getId()))
                .thenReturn(Optional.of(bookingCreate.getBooker()));
        when(itemRepository.findById(bookingCreate.getItem().getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.save(bookingCreate))
                .thenReturn(bookingCreate);

        final BookingException exception = assertThrows(
                BookingException.class,
                () -> bookingService.createBooking(
                        bookingCreate.getBooker().getId(),
                        BookingMapper.toBookingCreateDto(bookingCreate)));

        assertEquals("Некорректное время окончания бронирования.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createBookingWithInvalidStatusTest:" +
            " создание бронирования c статусом available = false")
    void createBookingWithInvalidStatusTest() {
        Item itemCreate = new Item(
                3L,
                "Item One",
                "Description item one",
                false,
                owner,
                null);

        Booking bookingCreate = new Booking(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                itemCreate,
                booker,
                BookingStateEnum.WAITING);

        when(userRepository.findById(bookingCreate.getBooker().getId()))
                .thenReturn(Optional.of(bookingCreate.getBooker()));
        when(itemRepository.findById(itemCreate.getId()))
                .thenReturn(Optional.of(itemCreate));

        when(bookingRepository.save(bookingCreate))
                .thenReturn(bookingCreate);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(
                        bookingCreate.getBooker().getId(),
                        BookingMapper.toBookingCreateDto(bookingCreate)));

        assertEquals("Вещь 3 не доступна для бронирования.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода patchBookingTest: обновление бронирования")
    void patchBookingTest() {
        Booking bookingPatch = new Booking(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                booker,
                BookingStateEnum.WAITING);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingPatch);
        when(bookingRepository.findById(bookingPatch.getId()))
                .thenReturn(Optional.of(bookingPatch));

        BookingDto bookingDto = bookingService.patchBooking(
                owner.getId(),
                bookingPatch.getId(),
                true);

        assertNotNull(bookingDto);
        assertEquals(bookingPatch.getStatus(), bookingDto.getStatus());
        assertEquals(bookingPatch.getId(), bookingDto.getId());
    }

    @Test
    @DisplayName("Вызов метода patchBookingTest: обновление бронирования c approved=false")
    void patchBookingApprovedFalseTest() {
        Booking bookingPatch = new Booking(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                booker,
                BookingStateEnum.WAITING);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingPatch);
        when(bookingRepository.findById(bookingPatch.getId()))
                .thenReturn(Optional.of(bookingPatch));

        BookingDto bookingDto = bookingService.patchBooking(
                owner.getId(),
                bookingPatch.getId(),
                false);

        assertNotNull(bookingDto);
        assertEquals(bookingPatch.getStatus(), bookingDto.getStatus());
        assertEquals(bookingPatch.getId(), bookingDto.getId());
    }

    @Test
    @DisplayName("Вызов метода patchBookingWithInvalidStatusTest:" +
            " обновление бронирования не верным id владельца вещи")
    void patchBookingWithInvalidOwnerTest() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.patchBooking(
                        booker.getId(),
                        booking.getId(),
                        true));

        assertEquals("Подтвердить бронирование может только владелец вещи.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода patchBookingWithStatusApprovedTest:" +
            " обновление бронирования со статусом APPROVED")
    void patchBookingWithStatusApprovedTest() {
        Booking bookingPatch = new Booking(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                booker,
                BookingStateEnum.APPROVED);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findById(bookingPatch.getId()))
                .thenReturn(Optional.of(bookingPatch));

        final BookingException exception = assertThrows(
                BookingException.class,
                () -> bookingService.patchBooking(
                        owner.getId(),
                        bookingPatch.getId(),
                        true));

        assertEquals("Бронирование уже было подтверждено.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода patchBookingWithStatusNullTest:" +
            " обновление бронирования с APPROVED=null")
    void patchBookingWithStatusNullTest() {
        Booking bookingPatch = new Booking(
                2L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                item,
                booker,
                BookingStateEnum.WAITING);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findById(bookingPatch.getId()))
                .thenReturn(Optional.of(bookingPatch));

        final BookingException exception = assertThrows(
                BookingException.class,
                () -> bookingService.patchBooking(
                        owner.getId(),
                        bookingPatch.getId(),
                        null));

        assertEquals("Необходимо указать статус возможности аренды (approved).", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода deleteByIdTest: удаление бронирования по id")
    void removeBookingByIdTest() {
        bookingService.removeBookingById(booking.getId());
    }
}