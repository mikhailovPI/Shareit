package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping
    public List<ItemDtoWithBooking> getItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("GetMapping/Получение всех вещей пользователя с id: " + userId);
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping(value = "/{itemId}")
    public ItemDtoWithBooking getItemById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        log.info("GetMapping/Получение вещи по id: " + itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> getItemSearch(
            @RequestParam(name = "text") String text,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("GetMapping/Поиск вещи по тексту: " + text);
        return itemService.getItemSearch(text, from, size);
    }

    @PostMapping
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {
        log.info("PostMapping/Создание вещи:" + itemDto +
                " пользователя с id: " + userId);
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto createComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto) {
        return commentService.createComment(commentDto, itemId, userId);
    }

    @DeleteMapping(value = "/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        log.info("DeleteMapping/Удаление вещи по id: " + itemId);
        itemService.removeItemById(itemId);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto patchItem(
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId) {
        log.info("PatchMapping/Обновление вещи с id: " + itemId +
                " обновляемая часть: " + itemDto +
                " пользователь с id: " + userId);
        return itemService.patchItem(itemDto, userId, itemId);
    }
}