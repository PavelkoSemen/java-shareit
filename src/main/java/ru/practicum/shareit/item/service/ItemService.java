package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemBookingDto> getItemsFromOwner(long ownerId);

    List<ItemDto> getItemsByFilter(long bookerId, String filter);

    ItemBookingDto getItem(long ownerId, long itemId);

    ItemDto createItem(long ownerId, ItemDto itemDto);

    ItemDto updateItemParameter(long ownerId, long itemId, ItemDto itemDto);

    CommentDto createComment(long bookerId, long itemId, CommentDto commentDto);
}