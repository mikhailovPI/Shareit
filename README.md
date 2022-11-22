# Сервис по бронированию вещей

**Программа позволяет производить бронирование вещи, оставлять комментарии к вещи и делать запрос на бронирования вещи.**

**Используемые стек: Java 11, Spring Boot, Spring JPA, Maven, PostgreSQL, Docker**

**Программа имеет два сервиса: geateway и server.**

Сервис geateway предназначен для валидация запросов. 
Сервис server включает в себя основную логику приложения.
Взаимодействие сервисов реализовано с помощью Сostume Client - наследуемый от BaseClient.
Запросы в БД производятся с использованием Spring JPA.

**Необходимо запускать docker-compose.yml на следующих портах:**
- gateway: ports - 8080:8080
- server:  ports - 9090:9090
- db:      ports - 6541:5432

Данные хранятся в БД. Схема БД представлена ниже.

Примеры Endpoint запросов (программа написана на Java):

```java

    @GetMapping(value = "/owner")
    public List<BookingDto> getAllBookingItemsUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        log.info("GetMapping/Получение всех бронирований для вещей пользователя с id: " + userId);
        return bookingService.getAllBookingItemsUser(userId, stateParam, from, size);
    }
```
