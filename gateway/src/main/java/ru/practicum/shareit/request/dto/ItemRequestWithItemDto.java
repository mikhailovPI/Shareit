package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestWithItemDto {

    Long id;

    String description;

    LocalDateTime created;

}
