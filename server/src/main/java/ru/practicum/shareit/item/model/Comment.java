package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments", schema = "public")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Long id;

    @Column(name = "comment_text", nullable = false)
    String text;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    Item item;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    User author;

    @Column(name = "created")
    LocalDateTime created;
}
