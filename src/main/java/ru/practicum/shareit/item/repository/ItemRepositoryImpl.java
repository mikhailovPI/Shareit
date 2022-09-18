package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private long id = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> getAllItem(Long userId) {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ValidationException(String.format("Вещь с %s не существует.", itemId));
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemSearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Item createItem(Item item) {

        item.setId(++id);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public void removeItem(Long id) {
        if (!items.containsKey(id)) {
            throw new ValidationException(String.format("Вещь с %s не существует.", id));
        }
        items.remove(id);
    }

    @Override
    public Item patchItem(Item item, Long itemId) {
        Item itemInMemory = items.get(itemId);
        itemInMemory.setName(item.getName());
        itemInMemory.setDescription(item.getDescription());
        itemInMemory.setAvailable(item.getAvailable());

        items.put(itemId, itemInMemory);
        return items.get(itemId);
    }

    @Override
    public Item patchItemAvailable(Item item, Long itemId) {
        Item itemInMemory = items.get(itemId);
        itemInMemory.setAvailable(item.getAvailable());

        items.put(itemId, itemInMemory);
        return items.get(itemId);
    }

    @Override
    public Item patchItemName(Item item, Long itemId) {
        Item itemInMemory = items.get(itemId);
        itemInMemory.setName(item.getName());

        items.put(itemId, itemInMemory);
        return items.get(itemId);
    }

    @Override
    public Item patchItemDescription(Item item, Long itemId) {
        Item itemInMemory = items.get(itemId);
        itemInMemory.setDescription(item.getDescription());

        items.put(itemId, itemInMemory);
        return items.get(itemId);
    }
}
