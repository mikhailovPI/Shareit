package ru.practicum.shareit.booking.controller;

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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateEnum;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private Booking booking;
    private User owner;
    private User booker;
    private Item item;
    private ItemRequest itemRequest;

    private ResultMatcher resultMatcher = content().json("{" +
            "\"id\": 1," +
            " \"item\": {\"id\": 1,\"name\": \"Item One\"," +
            " \"description\": \"Description item one\", \"available\": true," +
            " \"owner\": {\"id\": 1,\"name\": \"Name One\",\"email\": \"NameOne@gmail.com\"}," +
            " \"requestId\": null}," +
            " \"booker\": {\"id\": 2,\"name\": \"Name Two\",\"email\": \"NameTwo@gmail.com\"}}");

    private ResultMatcher resultMatcherList = content().json("[{" +
            "\"id\": 1," +
            " \"item\": {\"id\": 1,\"name\": \"Item One\"," +
            " \"description\": \"Description item one\", \"available\": true," +
            " \"owner\": {\"id\": 1,\"name\": \"Name One\",\"email\": \"NameOne@gmail.com\"}," +
            " \"requestId\": null}," +
            " \"booker\": {\"id\": 2,\"name\": \"Name Two\",\"email\": \"NameTwo@gmail.com\"}}]");

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        mapper.registerModule(new JavaTimeModule());

        owner = new User(
                1L,
                "NameOne@gmail.com",
                "Name One");
        booker = new User(
                2L,
                "NameTwo@gmail.com",
                "Name Two");
        item =  new Item(
                1L,
                "Item One",
                "Description item one",
                true,
                owner,
                null);
        booking = new Booking(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                item,
                booker,
                BookingStateEnum.APPROVED);

        itemRequest = new ItemRequest(
                1L,
                "Description item request one",
                booker,
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Вызов метода getAllBookingsTest: получение всех бронирований")
    void getAllBookingsTest() throws Exception {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        BookingDto bookingDto = toBookingDto(booking);
        bookingDtoList.add(bookingDto);
        when(bookingService.getAllBookings(bookingDto.getBooker().getId(), "ALL", 0, 20))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookingDto.getBooker().getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(resultMatcherList);
    }

    @Test
    @DisplayName("Вызов метода getAllBookingItemsUserTest: получение всех бронирований пользователя")
    void getAllBookingItemsUserTest() throws Exception {
        List<BookingDto> bookingDtoList = new ArrayList<>();
        BookingDto bookingDto = toBookingDto(booking);
        bookingDtoList.add(bookingDto);
        when(bookingService.getAllBookingItemsUser(bookingDto.getItem().getOwner().getId(),
                "ALL", 0, 20))
                .thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", bookingDto.getItem().getOwner().getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(resultMatcherList);
    }

    @Test
    @DisplayName("Вызов метода getBookingByIdTest: получение бронирования по id")
    void getBookingByIdTest() throws Exception {
        BookingDto bookingDto = toBookingDto(booking);
        when(bookingService.getBookingById(bookingDto.getBooker().getId(), booking.getId()))
                .thenReturn(bookingDto);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", bookingDto.getBooker().getId()))
                .andExpect(status().isOk())
                .andExpect(resultMatcher);
    }

    @Test
    @DisplayName("Вызов метода createBookingTest: создание бронирования")
    void createBookingTest() throws Exception {
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(5));
        BookingCreateDto bookingDtoSimple = BookingMapper.toBookingCreateDto(booking);
        BookingDto bookingDto = toBookingDto(booking);

        when(bookingService.createBooking(booker.getId(), bookingDtoSimple))
                .thenReturn(bookingDto);
        mockMvc.perform(post("/bookings", booker.getId())
                        .content(mapper.writeValueAsString(bookingDtoSimple))
                        .header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(resultMatcher);
    }

    @Test
    @DisplayName("Вызов метода patchBookingTest: обновление бронирования")
    void patchBookingTest() throws Exception {
        booking.setStatus(BookingStateEnum.REJECTED);
        BookingDto bookingDto = toBookingDto(booking);
        bookingDto.setStatus(BookingStateEnum.APPROVED);
        when(bookingService.patchBooking(
                booking.getItem().getOwner().getId(),
                booking.getId(),
                true))
                .thenReturn(bookingDto);
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(resultMatcher);
    }

    @Test
    @DisplayName("Вызов метода deleteByIdTest: удаление бронирования по id")
    void deleteByIdTest() throws Exception {
        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isOk());
    }
}