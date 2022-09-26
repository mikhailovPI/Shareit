package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAllItem(Long userId);

    Item getItemById(Long itemId);

    List<Item> getItemSearch(String text);

    Item createItem(Item item);

    void removeItem(Long id);

    Item patchItem(Item item, Long itemId);

    Item patchItemAvailable(Item item, Long itemId);

    Item patchItemName(Item item, Long itemId);

    Item patchItemDescription(Item item, Long itemId);
}
