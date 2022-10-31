package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items", schema = "public")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    Long id;

    @Column(name = "item_name")
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "available")
    Boolean available;

    @OneToOne(optional = false)
    @JoinColumn(name = "owner_id")
    User owner;

    @Column(name = "request_id")
    Long requestId;
}
