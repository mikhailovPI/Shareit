package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStateEnum;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class BookingDto {

    private Long id;

    @FutureOrPresent
    private LocalDateTime start;

    @FutureOrPresent
    private LocalDateTime end;

    private Item item;

    private User booker;

    private BookingStateEnum status;
}
