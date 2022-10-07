package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {
    List<ItemDtoWithBooking> getAllItems(Long userId);

    ItemDtoWithBooking getItemById(Long userId, Long itemId);

    List<ItemDto> getItemSearch(String text);

    ItemDto createItem(ItemDto itemDto, Long userId);

    void removeItemById(Long id);

    ItemDto patchItem(ItemDto itemDto, Long userId, Long id);

}