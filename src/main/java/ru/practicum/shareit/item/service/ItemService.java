package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsFromOwner(long ownerId);

    List<ItemDto> getItemsByFilter(long bookerId, String filter);

    ItemDto getItem(long itemId);

    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto updateItemParameter(long ownerId, long itemId, ItemDto itemDto);
}