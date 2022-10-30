package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Service
public interface BookingService {

    List<BookingDto> getAllBookings(Long userId, String stateParam, int from, int size);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookingItemsUser(Long userId, String stateParam, int from, int size);

    BookingDto createBooking(Long userId, BookingCreateDto bookingDto);

    BookingDto patchBooking(Long userId, Long bookingId, Boolean approved);

    void removeBookingById(Long bookingId);
}
