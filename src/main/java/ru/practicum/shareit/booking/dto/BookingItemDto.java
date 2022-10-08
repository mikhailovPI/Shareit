package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class BookingItemDto {

    private Long id;

    private Long bookerId;
}
