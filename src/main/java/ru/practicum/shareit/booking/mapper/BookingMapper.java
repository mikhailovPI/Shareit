package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateEnum;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItem(),
                bookingDto.getBooker(),
                bookingDto.getStatus());
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public static Booking toBookingCreate(BookingCreateDto bookingDtoTest) {
        return new Booking(
                bookingDtoTest.getId(),
                bookingDtoTest.getStart(),
                bookingDtoTest.getEnd(),
                null,
                null,
                BookingStateEnum.WAITING);
    }

    public static BookingItemDto toBookingDtoForItem(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId());
    }
}
