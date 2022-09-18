package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userRepository.getAllUsers()) {
            UserDto userDto = UserMapper.toUserDto(user);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() == null) {
            throw new ValidationException(String.format("E-mail не должен быть пустым."));
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Введен некорректный e-mail.");
        }
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public void removeUser(Long id) {
        userRepository.removeUser(id);
    }

    @Override
    public UserDto patchUser(UserDto userDto, Long id) {
        User user = UserMapper.toUser(userDto);
        if (userDto.getEmail() != null && userDto.getName() == null) {
            return UserMapper.toUserDto(userRepository.patchUserEmail(user, id));
        } else if (userDto.getName() != null && userDto.getEmail() == null) {
            return UserMapper.toUserDto(userRepository.patchUserName(user, id));
        } else {
            return UserMapper.toUserDto(userRepository.patchUser(user, id));
        }
    }
}