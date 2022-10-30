package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.PageRequestOverride;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateEnum;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(
            Long userId,
            PageRequestOverride pageRequest);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Long userId,
            LocalDateTime time,
            PageRequestOverride pageRequest);

    List<Booking> findBookingsByBookerIdAndStatusOrderByStartDesc(
            Long userId,
            BookingStateEnum status,
            PageRequestOverride pageRequest);

    List<Booking> findBookingsByBookerIdAndEndIsBeforeOrderByStartDesc(
            Long userId,
            LocalDateTime time,
            PageRequestOverride pageRequest);

    @Query("select b " +
            "from Booking b left join User as us on b.booker.id = us.id " +
            "where us.id = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start DESC")
    List<Booking> findCurrentBookingsByBookerIdOrderByStartDesc(
            Long userId,
            LocalDateTime time,
            PageRequestOverride pageRequest);

    List<Booking> searchBookingByItemOwnerIdOrderByStartDesc(Long userId,
                                             PageRequestOverride pageRequest);

    List<Booking> searchBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(
            Long id,
            LocalDateTime time,
            PageRequestOverride pageRequest);

    List<Booking> findBookingsByItemOwnerIdOrderByStartDesc(
            Long id,
            PageRequestOverride pageRequest);

    @Query("select b " +
            "from Booking b left join Item as i on b.item.id = i.id " +
            "left join User as us on i.owner.id = us.id " +
            "where us.id = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start DESC")
    List<Booking> findCurrentBookingsByItemOwnerIdOrderByStartDesc(
            Long userId,
            LocalDateTime time,
            PageRequestOverride pageRequest);

    List<Booking> findCurrentBookingsByItemOwnerId(
            Long userId,
            LocalDateTime time,
            PageRequestOverride pageRequest);


    List<Booking> findBookingsByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
            Long userId,
            LocalDateTime time,
            PageRequestOverride pageRequest);

    List<Booking> findBookingsByItemOwnerIdAndEndIsBefore(
            Long userId,
            LocalDateTime time,
            PageRequestOverride pageRequest);

    List<Booking> searchBookingByBookerIdAndItemIdAndEndIsBefore(
            Long id,
            Long itemId,
            LocalDateTime time);

    List<Booking> findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(
            Long id,
            LocalDateTime time);

    List<Booking> findBookingsByItemIdAndStartIsAfterOrderByStartDesc(
            Long id,
            LocalDateTime time);
}