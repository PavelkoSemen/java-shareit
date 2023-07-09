package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsFromUser(@RequestHeader("X-Sharer-User-Id") @NotNull long ownerId) {
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
}