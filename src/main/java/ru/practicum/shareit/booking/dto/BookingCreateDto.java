package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class BookingCreateDto {

    private Long id;

    @NotNull(message = "Поле не может быть пустым")
    private LocalDateTime start;

    @NotNull(message = "Поле не может быть пустым")
    private LocalDateTime end;

    private Long itemId;
}


