package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.EntityAccessException;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.error.RequestParameterException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.CommentMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemBookingCommentDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemBookingDto;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    /**
     * В принципе можно было сделать либо таким способом, получать List<Booking> и резать его для каждого Item,
     * либо для каждого Item делать запрос в БД, но это может породить большое количество запросов.
     * Но если бы это был боевой проект, я бы просто использовал аннотацию OneToMany и спокойно получил список
     * List<Booking> через один запрос.
     *
     * @param ownerId - владелец Item
     * @return List<ItemBookingDto>
     */
    @Override
    public List<ItemBookingDto> getItemsFromOwner(long ownerId) {
        log.info("Получение вещей пользователя с id {}", ownerId);
        List<ItemBookingDto> itemsBooking = new ArrayList<>();
        final List<Item> items = itemRepository.findItemByOwnerId(ownerId, Sort.by(Sort.Direction.ASC, "id"));
        final List<Long> itemsId = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        final List<Booking> bookings = bookingRepository.findByItemsId(itemsId);

        items.forEach(i -> itemsBooking.add(
                toItemBookingDto(i, bookings.stream()
                        .filter(b -> b.getItem().getId() == i.getId())
                        .collect(Collectors.toList()))));

        return itemsBooking;
    }

    @Override
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
    public ItemBookingDto getItem(long ownerId, long itemId) {
        log.info("Получение предмета с id {}", itemId);
        final Item item = itemRepository.findItemByComments(itemId).orElseThrow(() ->
                new EntityNotFoundException("Предмета с id " + itemId + " не существует"));

        List<Booking> bookings = Collections.emptyList();

        if (item.getOwner().getId() == ownerId) {
            bookings = bookingRepository.findByItemIdAndStatus(itemId, BookingStatus.APPROVED);
        }
        return toItemBookingCommentDto(item, bookings);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public CommentDto createComment(long bookerId, long itemId, CommentDto commentDto) {

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(bookerId, itemId, LocalDateTime.now())) {
            throw new RequestParameterException("Не существует завершенного бронирования");
        }

        final User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new EntityNotFoundException("Пользователя с id " + bookerId + " не существует"));
        final Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Предмета с id " + itemId + " не существует"));

        final Comment comment = toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(booker);

        return toCommentDto(commentRepository.save(comment));
    }
}