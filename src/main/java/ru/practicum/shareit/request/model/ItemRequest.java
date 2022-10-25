package ru.practicum.shareit.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long id;

    @Column(name = "description")
    String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    User requestor;

    @Column(name = "created")
    LocalDateTime created;
}