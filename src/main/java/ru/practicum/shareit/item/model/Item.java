package ru.practicum.shareit.item.model;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Item {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private String request;
}
