package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.error.EntitySaveException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создание пользователя {}", userDto);
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new EntitySaveException("Дубликат почты у пользователя: " + userDto);
        }
        User user = toUser(userDto);
        return toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUserParameter(Long userId, UserDto userDto) {
        log.info("Обновление параметров пользователя {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователя с id " + userId + " не существует"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new EntitySaveException("Дубликат почты у пользователя: " + userDto);
            }

            user.setEmail(userDto.getEmail());
        }

        return toUserDto(user);
    }

    @Override
    public UserDto getUser(long userId) {
        log.info("Получение пользователя с id: {}", userId);
        return toUserDto(userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователя с id " + userId + " не существует")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Удаление пользователя с id: {}", userId);
        userRepository.deleteById(userId);
    }
}