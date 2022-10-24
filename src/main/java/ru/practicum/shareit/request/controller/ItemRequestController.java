package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestWithItemDto> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GetMapping/Получение всех запросов на добавление вещи");
        return itemRequestService.getAllItemRequest(userId);
    }

    @GetMapping(value = "/{requestId}")
    private ItemRequestWithItemDto getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("GetMapping/Получение запроса на добавление вещи по id: " + requestId
        + " пользователя: " + userId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }

    @GetMapping(value = "/all")
    private List<ItemRequestWithItemDto> getItemRequestOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("GetMapping/Получение запросв на добавление вещи другими пользователями:" +
                " параметры пагинации from: " + from + " size: " + size);
        return itemRequestService.getItemRequestOtherUsers(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("PostMapping/Создание пользователя: " + itemRequestDto);
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }
}