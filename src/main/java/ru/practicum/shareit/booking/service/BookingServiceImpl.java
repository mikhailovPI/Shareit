package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateEnum;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<BookingDto> getAllBookings(Long userId, String stateParam) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь %s не существует.", userId)));

        switch (BookingStateEnum.valueOf(stateParam)) {
            case CURRENT:
                return bookingRepository
                        .findCurrentBookingsByBookerIdOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findBookingsByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findBookingsByBookerIdAndStatusOrderByStartDesc(userId, BookingStateEnum.WAITING)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findBookingsByBookerIdAndStatusOrderByStartDesc(userId, BookingStateEnum.REJECTED)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository
                        .findByBookerIdOrderByStartDesc(userId)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Бронирование %s не существует.", bookingId)));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Пользователь %s не осуществлял бронирование.", userId));
        }
        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingItemsUser(Long userId, String stateParam) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь %s не существует.", userId)));
        List<BookingDto> bookingsUserList = bookingRepository.searchBookingByItemOwnerId(userId)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());

        if (bookingsUserList.isEmpty()) {
            throw new NotFoundException("У пользователя нет вещей");
        }

        switch (BookingStateEnum.valueOf(stateParam)) {
            case CURRENT:
                return bookingRepository
                        .findCurrentBookingsByItemOwnerIdOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findBookingsByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .searchBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findBookingsByItemOwnerIdOrderByStartDesc(userId)
                        .stream()
                        .filter(booking -> booking.getStatus().equals(BookingStateEnum.WAITING))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findBookingsByItemOwnerIdOrderByStartDesc(userId).stream()
                        .filter(booking -> booking.getStatus().equals(BookingStateEnum.REJECTED))
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                bookingsUserList.sort(Comparator.comparing(BookingDto::getStart).reversed());
                return bookingsUserList;
        }
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingCreateDto bookingDto) {
        Booking booking = BookingMapper.toBookingCreate(bookingDto);
        booking.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь %s не существует.", userId))));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь %s не существует.", bookingDto.getItemId())));

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец вещи не может забронировать свою вещь");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BookingException("Некорректное время окончания бронирования.");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingException("Некорректное время начала бронирования.");
        }
        if (item.getAvailable()) {
            booking.setItem(item);
            Booking bookingCreate = bookingRepository.save(booking);
            return BookingMapper.toBookingDto(bookingCreate);
        } else {
            throw new ValidationException(
                    String.format("Вещь %s не доступна для бронирования.", item.getId()));
        }
    }

    @Override
    @Transactional
    public BookingDto patchBooking(Long userId, Long bookingId, Boolean approved) {
        BookingDto bookingDto = toBookingDto(bookingRepository.findById(bookingId).orElseThrow());
        Booking booking = BookingMapper.toBooking(bookingDto);
        if (!userId.equals(bookingDto.getItem().getOwner().getId())) {
            throw new NotFoundException("Подтвердить бронирование может только владелец вещи");
        }
        if (booking.getStatus().equals(BookingStateEnum.APPROVED)) {
            throw new BookingException("Бронирование уже было подтверждено");
        }
        if (approved == null) {
            throw new BookingException("Необходимо указать статус возможности аренды (approved).");
        } else if (approved) {
            booking.setStatus(BookingStateEnum.APPROVED);
            Booking bookingSave = bookingRepository.save(booking);
            return toBookingDto(bookingSave);
        } else {
            booking.setStatus(BookingStateEnum.REJECTED);
            booking.setItem(bookingDto.getItem());
            Booking bookingSave = bookingRepository.save(booking);
            return toBookingDto(bookingSave);
        }
    }

    @Override
    public void removeBookingById(Long bookingId) {
        bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Бронирование %s не существует.", bookingId)));
        bookingRepository.deleteById(bookingId);
    }
}
