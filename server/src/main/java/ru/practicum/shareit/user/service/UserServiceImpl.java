
package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            UserDto userDto = UserMapper.toUserDto(user);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь %s не существует.", id))));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("E-mail не должен быть пустым.");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ValidationException("Введен некорректный e-mail.");
        }
        User user = UserMapper.toUser(userDto);
        User userCreate = userRepository.save(user);
        return UserMapper.toUserDto(userCreate);
    }

    @Override
    @Transactional
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto patchUser(UserDto userDto, Long id) {
        final User user = UserMapper.toUser(userDto);
        final User userUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь %s не существует.", id)));
        if (user.getEmail() != null && user.getName() == null) {
            userUpdate.setEmail(user.getEmail());
            userRepository.save(userUpdate);
            return UserMapper.toUserDto(userUpdate);
        } else if (user.getName() != null && user.getEmail() == null) {
            userUpdate.setName(user.getName());
            userRepository.save(userUpdate);
            return UserMapper.toUserDto(userUpdate);
        } else {
            userUpdate.setName(user.getName());
            userUpdate.setEmail(user.getEmail());
            userRepository.save(userUpdate);
            return UserMapper.toUserDto(userUpdate);
        }
    }
}
