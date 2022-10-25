package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Item item;
    private User user;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(
                1L,
                "NameOne@gmail.com",
                "Name One"));
        item = itemRepository.save(new Item(
                1L,
                "Item One",
                "Description item one",
                true,
                user,
                null));
        comment = commentRepository.save(new Comment(
                1L,
                "Text comment one",
                item,
                user,
                LocalDateTime.now()));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findAllCommentByItemIdTest() {
        final List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertSame(comment, comments.get(0));
    }
}