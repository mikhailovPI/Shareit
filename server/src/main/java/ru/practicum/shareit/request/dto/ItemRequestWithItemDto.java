package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestWithItemDto {

    Long id;

    String description;

    User requestor;

    LocalDateTime created;

    List<Item> items;
}
