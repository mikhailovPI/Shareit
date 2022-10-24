package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.PageRequestOverride;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User userOne;
    private User userTwo;
    private ItemRequest itemRequestOne;
    private Item itemOne;
    private PageRequestOverride pageRequest;

    @BeforeEach
    void createUser() {
        userOne = userRepository.save(new User(
                1L,
                "nameOne@gmail.com",
                "Name One"));

        userTwo = userRepository.save(new User(
                2L,
                "nameTwo@gmail.com",
                "Name Two"));

        itemRequestOne = itemRequestRepository.save(new ItemRequest(1L,
                "Description item request one",
                userTwo,
                LocalDateTime.now()));

        itemOne = itemRepository.save(new Item(1L,
                "Item One",
                "Description item one",
                true,
                userOne,
                itemRequestOne.getId()));

        pageRequest = PageRequestOverride.of(0, 20);
    }

    @Test
    void searchTest() {
        String text = "Description";
        final List<Item> items = itemRepository.search(text, pageRequest);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(itemOne, items.get(0));
    }

    @Test
    void findByRequestIdTest() {
        final List<Item> items = itemRepository.findByRequestId(itemRequestOne.getId());
        assertSame(userTwo, itemRequestOne.getRequestor());
        assertNotNull(items);
        assertEquals(1, items.size());
        assertSame(itemOne, items.get(0));
    }
}