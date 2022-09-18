package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> getAllUsers();

    User getUserById(Long id);

    User createUser(User user);

    void removeUser(Long id);

    User patchUser(User user, Long id);

    User patchUserName(User user, Long id);

    User patchUserEmail(User user, Long id);
}
