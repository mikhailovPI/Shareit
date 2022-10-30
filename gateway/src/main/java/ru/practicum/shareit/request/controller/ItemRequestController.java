package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GetMapping/Получение всех запросов на добавление вещи");
        return itemRequestClient.getAllItemRequest(userId);
    }

    @GetMapping(value = "/{requestId}")
    private ResponseEntity<Object> getItemRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {
        log.info("GetMapping/Получение запроса на добавление вещи по id: " + requestId
        + " пользователя: " + userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping(value = "/all")
    private ResponseEntity<Object> getItemRequestOtherUsers(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from и/или size");
        }
        log.info("GetMapping/Получение запросв на добавление вещи другими пользователями:" +
                " параметры пагинации from: " + from + " size: " + size);
        return itemRequestClient.getItemRequestOtherUsers(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null) {
            throw new ValidationException("Описание для создаваемого запроса не может быть пустым.");
        }
        log.info("PostMapping/Создание пользователя: " + itemRequestDto);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }
}