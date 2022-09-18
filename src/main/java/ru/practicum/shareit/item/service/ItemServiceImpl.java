package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException(String.format("Пользователя с id %s не существует.", userId));
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemRepository.getAllItem(userId)) {
            if (item.getOwnerId().equals(userId)) {
                ItemDto itemDto = ItemMapper.toItemDto(item);
                itemDtoList.add(itemDto);
            }
        }
        return itemDtoList;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemSearch(String text) {
        return itemRepository.getItemSearch(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException(String.format("Пользователя с id %s не существует.", userId));
        }
        Item item = ItemMapper.toItem(itemDto, userId);
        if (item.getName().isEmpty() || item.getDescription() == null || item.getAvailable() == null) {
            throw new ValidationException("Данно поле не может быть пустым.");
        }
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public void removeItem(Long id) {
        itemRepository.removeItem(id);
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long userId, Long itemId) {
        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException(String.format("Пользователя с id %s не существует.", userId));
        }
        if (!itemRepository.getItemById(itemId).getOwnerId().equals(userId)) {
            throw new NotFoundException(String.format("Пользователя с id %s владеет вещью.", userId));
        }
        Item item = ItemMapper.toItem(itemDto, userId);
        if (itemDto.getAvailable() != null && itemDto.getName() == null && itemDto.getDescription() == null) {
            return ItemMapper.toItemDto(itemRepository.patchItemAvailable(item, itemId));
        } else if (itemDto.getName() != null && itemDto.getAvailable() == null && itemDto.getDescription() == null) {
            return ItemMapper.toItemDto(itemRepository.patchItemName(item, itemId));
        } else if (itemDto.getDescription() != null && itemDto.getName() == null && itemDto.getAvailable() == null) {
            return ItemMapper.toItemDto(itemRepository.patchItemDescription(item, itemId));
        } else {
            return ItemMapper.toItemDto(itemRepository.patchItem(item, itemId));
        }
    }
}
