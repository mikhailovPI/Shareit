package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.PageRequestOverride;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDtoForItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDtoWithBooking;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDtoWithBooking> getAllItems(Long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from и/или size");
        }
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size);

        List<ItemDtoWithBooking> itemsDtoWithBookingList = itemRepository.findAll(pageRequest)
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDtoWithBooking)
                .collect(Collectors.toList());
        for (ItemDtoWithBooking itemDtoWithBooking : itemsDtoWithBookingList) {
            createLastAndNextBooking(itemDtoWithBooking);
            List<Comment> comments = commentRepository.findAllByItemId(itemDtoWithBooking.getId());
            if (!comments.isEmpty()) {
                itemDtoWithBooking.setComments(comments
                        .stream().map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()));
            }
        }
        itemsDtoWithBookingList.sort(Comparator.comparing(ItemDtoWithBooking::getId));
        return itemsDtoWithBookingList;
    }

    @Override
    public ItemDtoWithBooking getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                String.format("Вещь %s не существует.", itemId)));

        ItemDtoWithBooking itemDtoWithBooking = toItemDtoWithBooking(item);
        if (item.getOwner().getId().equals(userId)) {
            createLastAndNextBooking(itemDtoWithBooking);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        if (!comments.isEmpty()) {
            itemDtoWithBooking.setComments(comments
                    .stream().map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList())
            );
        }
        return itemDtoWithBooking;
    }

    @Override
    public List<ItemDto> getItemSearch(String text, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Переданы некорректные значения from и/или size");
        }
        PageRequestOverride pageRequest = PageRequestOverride.of(from, size);

        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text, pageRequest)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        if (itemDto.getName().isBlank() || itemDto.getDescription() == null || itemDto.getAvailable() == null) {
            throw new ValidationException("Данное поле не может быть пустым.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь %s не существует.", userId)));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemDto.getRequestId());
        }
        Item itemCreate = itemRepository.save(item);
        return ItemMapper.toItemDto(itemCreate);
    }

    @Override
    @Transactional
    public void removeItemById(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ItemDto patchItem(ItemDto itemDto, Long userId, Long itemId) {
        Item item = ItemMapper.toItem(itemDto);
        final Item itemUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь %s не существует.", itemId)));
        if (itemUpdate.getOwner().getId().equals(userId)) {
            if (item.getAvailable() != null && item.getName() == null && item.getDescription() == null) {
                itemUpdate.setAvailable(item.getAvailable());
                itemRepository.save(itemUpdate);
                return ItemMapper.toItemDto(itemUpdate);
            } else if (item.getName() != null && item.getAvailable() == null && item.getDescription() == null) {
                itemUpdate.setName(item.getName());
                itemRepository.save(itemUpdate);
                return ItemMapper.toItemDto(itemUpdate);
            } else if (item.getDescription() != null && item.getName() == null && item.getAvailable() == null) {
                itemUpdate.setDescription(item.getDescription());
                itemRepository.save(itemUpdate);
                return ItemMapper.toItemDto(itemUpdate);
            } else {
                itemUpdate.setName(item.getName());
                itemUpdate.setDescription(item.getDescription());
                itemUpdate.setAvailable(item.getAvailable());
                itemRepository.save(itemUpdate);
                return ItemMapper.toItemDto(itemUpdate);
            }
        } else {
            throw new NotFoundException(
                    String.format("Пользователь %s не владеет вещью.", userId));
        }
    }

    private void createLastAndNextBooking(ItemDtoWithBooking itemDtoWithBooking) {
        List<Booking> lastBookings = bookingRepository
                .findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(itemDtoWithBooking.getId(),
                        LocalDateTime.now());
        if (!lastBookings.isEmpty()) {
            BookingItemDto lastBooking = toBookingDtoForItem(lastBookings.get(0));
            itemDtoWithBooking.setLastBooking(lastBooking);
        }
        List<Booking> nextBookings = bookingRepository
                .findBookingsByItemIdAndStartIsAfterOrderByStartDesc(itemDtoWithBooking.getId(),
                        LocalDateTime.now());
        if (!nextBookings.isEmpty()) {
            BookingItemDto nextBooking = toBookingDtoForItem(nextBookings.get(0));
            itemDtoWithBooking.setNextBooking(nextBooking);
        }
    }
}