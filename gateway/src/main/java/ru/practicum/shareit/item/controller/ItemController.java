package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.client.CommentClient;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;
    private final CommentClient commentClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("GetMapping/Получение всех вещей пользователя с id: " + userId);
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from и/или size");
        }
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        log.info("GetMapping/Получение вещи по id: " + itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Object> getItemSearch(
            @RequestParam(name = "text") String text,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from и/или size");
        }
        log.info("GetMapping/Поиск вещи по тексту: " + text);
        return itemClient.getItemSearch(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {

        if (itemDto.getName().isBlank() || itemDto.getDescription() == null || itemDto.getAvailable() == null) {
            throw new ValidationException("Данное поле не может быть пустым.");
        }
        log.info("PostMapping/Создание вещи:" + itemDto +
                " пользователя с id: " + userId);
        return itemClient.createItem(itemDto, userId);
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Комментарий не может быть пустым.");
        }
        log.info("PostMapping/Создание комментария:" + commentDto +
                " вещи с id: " + itemId +
                " пользователя с id: " + userId);
        return commentClient.createComment(userId, itemId, commentDto);
    }

    @DeleteMapping(value = "/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        log.info("DeleteMapping/Удаление вещи по id: " + itemId);
        itemClient.removeItemById(itemId);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> patchItem(
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        log.info("PatchMapping/Обновление вещи с id: " + itemId +
                " обновляемая часть: " + itemDto +
                " пользователь с id: " + userId);
        return itemClient.patchItem(itemDto, userId, itemId);
    }
}