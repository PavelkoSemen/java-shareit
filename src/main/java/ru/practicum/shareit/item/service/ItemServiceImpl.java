package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EntityAccessException;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItemsFromOwner(long ownerId) {
        log.info("Получение вещей пользователя с id {}", ownerId);
        return itemRepository.findItemByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> getItemsByFilter(long bookerId, String filter) {
        log.info("Пользователь с id {} ищет вещи с фильтром {}", bookerId, filter);
        if (filter.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByFilter(filter.toUpperCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(long itemId) {
        log.info("Получение предмета с id {}", itemId);
        return toItemDto(itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Предмета с id " + itemId + " не существует")));
    }

    @Override
    public ItemDto createItem(long ownerId, ItemDto itemDto) {
        log.info("Создание предмета {} у пользователя c id {}", itemDto, ownerId);
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new EntityNotFoundException("Пользователя с id " + ownerId + " не существует"));

        Item item = toItem(itemDto);
        item.setOwner(owner);

        return toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItemParameter(long ownerId, long itemId, ItemDto itemDto) {
        log.info("Обновление предмета {} у пользователя c id {}", itemDto, ownerId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Предмета с id " + itemId + " не существует"));
        if (item.getOwner().getId() != ownerId) {
            throw new EntityAccessException("У пользователя с id " + ownerId + " нет доступа");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return toItemDto(item);
    }
}