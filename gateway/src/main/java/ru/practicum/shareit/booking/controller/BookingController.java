package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingStateEnum;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        BookingStateEnum state = BookingStateEnum.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from и/или size");
        }
        log.info("GetMapping/Получение всех бронирований пользователя с id: " + userId);
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<Object> getAllBookingItemsUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        BookingStateEnum state = BookingStateEnum.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from и/или size");
        }
        log.info("GetMapping/Получение всех бронирований для вещей пользователя с id: " + userId);
        return bookingClient.getAllBookingItemsUser(userId, state, from, size);
    }

    @GetMapping(value = "/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        log.info("GetMapping/Получение бронирования по id: " + bookingId +
                " для пользователя с id: " + userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingCreateDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BookingException("Некорректное время окончания бронирования.");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingException("Некорректное время начала бронирования.");
        }
        log.info("PostMapping/Создание бронирования:" + bookingDto +
                " пользователя с id: " + userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> patchBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("PatchMapping/Обновление бронирования с id: " + bookingId +
                " для пользователя с id: " + userId +
                " статус доступности вещи для бронирования: " + approved);
        if (approved == null) {
            throw new BookingException("Необходимо указать статус возможности аренды (approved).");
        }
        return bookingClient.patchBooking(userId, bookingId, approved);
    }

    @DeleteMapping("/{bookingId}")
    public void deleteById(@PathVariable Long bookingId) {
        log.info("DeleteMapping/Удаление бронирования по id: " + bookingId);
        bookingClient.removeBookingById(bookingId);
    }
}




