package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems(Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemSearch(String text);

    ItemDto createItem(ItemDto itemDto, Long userId);

    void removeItem(Long id);

    ItemDto patchItem(ItemDto itemDto,Long userId, Long id);

}
