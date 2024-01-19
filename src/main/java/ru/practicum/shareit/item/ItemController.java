package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ItemBookingDto getItem(@RequestHeader("X-Sharer-User-Id") @NotNull long ownerId, @PathVariable long itemId) {
        return itemService.getItem(ownerId, itemId);
    }

    @GetMapping
    public List<ItemBookingDto> getItemsFromUser(@RequestHeader("X-Sharer-User-Id") @NotNull long ownerId) {
        return itemService.getItemsFromOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") @NotNull long bookerId,
                                @RequestParam(name = "text") String filter) {
        return itemService.getItemsByFilter(bookerId, filter);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @NotNull long ownerId,
                              @RequestBody @Valid ItemDto itemDto) {
        return itemService.createItem(ownerId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItemParameter(@RequestHeader("X-Sharer-User-Id") @NotNull long ownerId,
                                       @PathVariable long itemId,
                                       @RequestBody ItemDto itemDto) {
        return itemService.updateItemParameter(ownerId, itemId, itemDto);
    }

    @PostMapping("{itemId}/comment")
    private CommentDto createComment(@RequestHeader("X-Sharer-User-Id") @NotNull long bookerId,
                                     @PathVariable long itemId,
                                     @RequestBody @Valid CommentDto commentDto) {

        return itemService.createComment(bookerId, itemId, commentDto);
    }
}