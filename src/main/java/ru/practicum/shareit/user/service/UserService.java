package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(long userId);

    UserDto createUser(UserDto userDto);

    UserDto updateUserParameter(Long userId, UserDto userDto);

    void deleteUser(long userId);
}