package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private User userOne;
    private User userTwo;

    @BeforeEach
    void createObjects() {
        userRepository = mock(UserRepository.class);
        when(userRepository.save(any()))
                .then(invocation -> invocation.getArgument(0));

        userService = new UserServiceImpl(userRepository);

        userOne = new User(
                1L,
                "nameOne@gmail.com",
                "Name One");
        userTwo = new User(
                2L,
                "nameTwo@gmail.com",
                "Name Two");
    }

    @Test
    @DisplayName("Вызов метода getAllUsersTest: получение всех пользователей")
    void getAllUsersTest() {
        List<User> userList = List.of(userOne, userTwo);

        when(userRepository.findAll())
                .thenReturn(List.of(userOne, userTwo));

        var userResultList = userService.getAllUsers();
        assertNotNull(userResultList);
        assertEquals(userList.size(), userResultList.size());
    }

    @Test
    @DisplayName("Вызов метода getUserByIdTest: получение пользователя по id")
    void getUserByIdTest() {
        when(userRepository.findById(userOne.getId()))
                .thenReturn(Optional.of(userOne));

        var userResult = userService.getUserById(userOne.getId());

        assertNotNull(userResult);
        assertEquals(userOne.getId(), userResult.getId());
        assertEquals(userOne.getEmail(), userResult.getEmail());
        assertEquals(userOne.getName(), userResult.getName());
    }

    @Test
    @DisplayName("Вызов метода getUserWithInvalidIdTest: получение пользователя по не верному id")
    void getUserWithInvalidIdTest() {
        long id = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(id));

        assertEquals("Пользователь 50 не существует.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createUserTest: создание пользователя")
    void createUserTest() {
        when(userRepository.save(any(User.class)))
                .thenReturn(userOne);

        UserDto userResult = userService.createUser(toUserDto(userOne));

        assertNotNull(userResult);
        assertEquals(userOne.getId(), userResult.getId());
        assertEquals(userOne.getEmail(), userResult.getEmail());
        assertEquals(userOne.getName(), userResult.getName());
    }


    @Test
    @DisplayName("Вызов метода createUserWithoutEmailTest: создание пользователя с пустым email")
    void createUserWithoutEmailTest() {
        User user = new User(3L, null, "Name Three");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.createUser(UserMapper.toUserDto(user)));

        assertEquals("E-mail не должен быть пустым.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода createUserNotCorrectEmailTest: создание пользователя с некорректным email")
    void createUserNotCorrectEmailTest() {
        User user = new User(3L, "NameTwoСom", "Name Three");

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.createUser(UserMapper.toUserDto(user)));

        assertEquals("Введен некорректный e-mail.", exception.getMessage());
    }

    @Test
    @DisplayName("Вызов метода removeUserTest: удаление пользователя")
    void removeUserTest() {
        userService.removeUser(userOne.getId());

        assertNotNull(userOne);
    }

    @Test
    @DisplayName("Вызов метода updateUserTest: обновление имени и email пользователя")
    void updateUserTest() {
        User user = new User();
        user.setEmail("nameFive@gmail.com");
        user.setName("Name Five");

        when(userRepository.save(any(User.class)))
                .thenReturn(userOne);

        when(userRepository.findById(userOne.getId()))
                .thenReturn(Optional.of(userOne));
        userOne.setName(user.getName());
        userOne.setEmail(user.getEmail());

        when(userRepository.save(any(User.class)))
                .thenReturn(userOne);

        var userResult = userService.patchUser(UserMapper.toUserDto(user), userOne.getId());

        assertNotNull(userResult);
        assertEquals(userOne.getEmail(), userResult.getEmail());
        assertEquals(userOne.getName(), userResult.getName());
    }

    @Test
    @DisplayName("Вызов метода updateUserTest: обновление имени пользователя")
    void updateUserNameTest() {
        User userThree = new User();
        userThree.setName("Name Five");

        when(userRepository.findById(userOne.getId()))
                .thenReturn(Optional.of(userOne));
        userOne.setName(userThree.getName());

        when(userRepository.save(userOne))
                .thenReturn(userOne);

        var userResult = userService.patchUser(UserMapper.toUserDto(userThree), userOne.getId());

        assertNotNull(userResult);
        assertEquals(userOne.getEmail(), userResult.getEmail());
        assertEquals(userOne.getName(), userResult.getName());
    }

    @Test
    @DisplayName("Вызов метода updateUserTest: обновление email пользователя")
    void updateUserEmailTest() {
        User userThree = new User();
        userThree.setEmail("nameFive@gmail.com");

        when(userRepository.findById(userOne.getId()))
                .thenReturn(Optional.of(userOne));
        userOne.setEmail(userThree.getEmail());

        when(userRepository.save(userOne))
                .thenReturn(userOne);

        var userResult = userService.patchUser(UserMapper.toUserDto(userThree), userOne.getId());

        assertNotNull(userResult);
        assertEquals(userOne.getEmail(), userResult.getEmail());
        assertEquals(userOne.getName(), userResult.getName());
    }

    @Test
    @DisplayName("Вызов метода updateUserTest: обновление пользователя по не верному id")
    void updateUserWithInvalidIdTest() {
        User userThree = new User();
        userThree.setName("Name Five");
        userThree.setEmail("nameFive@gmail.com");

        long id = 50;

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.patchUser(UserMapper.toUserDto(userThree), id));

        assertEquals("Пользователь 50 не существует.", exception.getMessage());
    }
}