package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingStateEnum {
    //Все
    ALL,
    //Текущие
    CURRENT,
    //Завершенные
    PAST,
    //Будущие
    FUTURE,
    //Ожидающие подтверждения
    WAITING,
    //Отклонённые
    REJECTED,
    //Одобренный
    APPROVED,
    //Отмененный
    CANCELED;

    public static Optional<BookingStateEnum> from(String stringState) {
        for (BookingStateEnum state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
