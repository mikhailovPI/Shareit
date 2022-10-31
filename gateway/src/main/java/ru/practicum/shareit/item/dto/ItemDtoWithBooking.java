package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoWithBooking {

    Long id;

    @NotBlank(message = "Имя не может быть пустым")
    String name;

    @NotBlank(message = "Описание не может быть пустым")
    String description;

    Boolean available;

    BookingItemDto lastBooking;

    BookingItemDto nextBooking;

    List<CommentDto> comments;
}