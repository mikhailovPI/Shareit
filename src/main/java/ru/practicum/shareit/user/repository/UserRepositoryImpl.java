package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityFoundException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {

    private long userId = 0;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersEmail = new HashSet<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователя с %s не существует.", id));
        }
        return users.get(id);
    }

    @Override
    public User createUser(User user) throws ValidationException {

        if (usersEmail.contains(user.getEmail())) {
            throw new ValidationException("Пользователь с %d уже существует.");
        }

        user.setId(++userId);
        users.put(user.getId(), user);
        usersEmail.add(user.getEmail());
        return users.get(user.getId());
    }

    @Override
    public void removeUser(Long id) {
        if (!users.containsKey(id)) {
            throw new ValidationException(String.format("Пользователя с %s не существует.", id));
        }
        usersEmail.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public User patchUser(User user, Long id) {
        if (usersEmail.contains(user.getEmail())) {
            throw new EntityNotFoundException(String.format("Пользователя с %s не существует.", user.getEmail()));
        }
        User userInMemory = users.get(id);
        usersEmail.remove(userInMemory.getEmail());

        userInMemory.setName(user.getName());
        userInMemory.setEmail(user.getEmail());
        users.put(userInMemory.getId(), userInMemory);
        usersEmail.add(userInMemory.getEmail());
        return userInMemory;
    }

    @Override
    public User patchUserName(User user, Long id) {
        User userInMemory = users.get(id);

        userInMemory.setName(user.getName());
        users.put(userInMemory.getId(), userInMemory);
        return userInMemory;
    }

    @Override
    public User patchUserEmail(User user, Long id) {
        if (usersEmail.contains(user.getEmail())) {
            throw new EntityFoundException(String.format("Пользователя с %s не существует.", user.getEmail()));
        }
        User userInMemory = users.get(id);
        usersEmail.remove(userInMemory.getEmail());

        userInMemory.setEmail(user.getEmail());
        users.put(userInMemory.getId(), userInMemory);
        usersEmail.add(userInMemory.getEmail());
        return userInMemory;
    }
}