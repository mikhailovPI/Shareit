package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long request;

}
