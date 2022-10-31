package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestWithItemDto> getAllItemRequest(Long userId);

    ItemRequestWithItemDto getItemRequestById(Long userId, Long requestId);

    List<ItemRequestWithItemDto> getItemRequestOtherUsers(Long userId, int from, int size);

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);
}
