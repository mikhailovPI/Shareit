package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private UserDto userDto;

    @BeforeEach
    void createUserDto() {
        userDto = new UserDto(1L,
                "NameOne@gmail.com",
                "Name One");
    }

    @Test
    @DisplayName("Вызов метода getAllUsersTest: получение всех пользователей")
    void getAllUsersTest() throws Exception {
        List<UserDto> users = new ArrayList<>();

        users.add(userDto);

        when(userService.getAllUsers())
                .thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                        "\"id\": 1," +
                        "\"email\": \"NameOne@gmail.com\"," +
                        "\"name\": \"Name One\"}]"));
    }

    @Test
    @DisplayName("Вызов метода getUserByIdTest: получение пользователя по id")
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(1L))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"id\": 1," +
                        "\"email\": \"NameOne@gmail.com\"," +
                        "\"name\": \"Name One\"}"));
    }

    @Test
    @DisplayName("Вызов метода createUserTest: создание пользователя")
    void createUserTest() throws Exception {
        when(userService.createUser(userDto))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(new ObjectMapper().writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    @DisplayName("Вызов метода removeUserTest: удаление пользователя")
    void removeUserTest() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .content(new ObjectMapper().writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Вызов метода patchUserTest: обновление пользователя")
    void patchUserTest() throws Exception {
        UserDto userDtoTwo = new UserDto(1L,
                "NameTwo@gmail.com",
                "Name Two");

        userService.createUser(userDto);

        when(userService.patchUser(userDtoTwo, 1L))
                .thenReturn(userDtoTwo);

        mockMvc.perform(patch("/users/1")
                        .content(new ObjectMapper().writeValueAsString(userDtoTwo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"id\": 1," +
                        "\"email\": \"NameTwo@gmail.com\"," +
                        "\"name\": \"Name Two\"}"));
    }
}