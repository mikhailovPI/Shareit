package ru.practicum.shareit.booking.model;

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

    public static BookingStateEnum from(String stateParam) {
        for (BookingStateEnum value : BookingStateEnum.values()) {
            if (value.name().equals(stateParam)) {
                return value;
            }
        }
        return null;
    }
}
