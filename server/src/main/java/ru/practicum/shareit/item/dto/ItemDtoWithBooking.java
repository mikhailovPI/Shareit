package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithBooking {

    Long id;

    String name;

    String description;

    Boolean available;

    BookingItemDto lastBooking;

    BookingItemDto nextBooking;

    List<CommentDto> comments;
}