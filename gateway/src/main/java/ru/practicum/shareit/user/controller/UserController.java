package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GetMapping/Получение всех пользователей");
        return userClient.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("GetMapping/Получение пользователя по id: " + id);
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("E-mail не должен быть пустым.");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Введен некорректный e-mail.");
        }
        log.info("PostMapping/Создание пользователя: " + userDto);
        return userClient.createUser(userDto);
    }

    @DeleteMapping(value = "/{id}")
    public void removeUser(@PathVariable Long id) {
        log.info("DeleteMapping/Удаление пользователя по id: " + id);
        userClient.removeUser(id);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> patchUser(
            @RequestBody UserDto userDto,
            @PathVariable Long id) {
        log.info("PatchMapping/Обновление пользователя с id: " + id +
                " обновляемая часть: " + userDto);
        return userClient.patchUser(userDto, id);
    }
}