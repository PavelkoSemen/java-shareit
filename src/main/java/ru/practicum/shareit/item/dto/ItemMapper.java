package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toShortBookingDto;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable()).build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription()).build();
    }

    public static ItemBookingDto toItemBookingDto(Item item, List<Booking> bookings) {

        Booking lastBooking = null;
        Booking nextBooking = null;

        for (Booking booking : bookings) {
            if (!booking.getStart().isAfter(LocalDateTime.now())) {
                lastBooking = booking;
                break;
            }
            nextBooking = booking;
        }
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.isAvailable())
                .description(item.getDescription())
                .lastBooking(lastBooking != null ? toShortBookingDto(lastBooking) : null)
                .nextBooking(nextBooking != null ? toShortBookingDto(nextBooking) : null)
                .build();
    }

    /**
     * Возможно стоило создать еще один DTO ItemBookingCommentDto
     *
     * @param item     - предмет
     * @param bookings - бронирование
     * @return ItemBookingDto
     */
    public static ItemBookingDto toItemBookingCommentDto(Item item, List<Booking> bookings) {

        Booking lastBooking = null;
        Booking nextBooking = null;

        for (Booking booking : bookings) {
            if (!booking.getStart().isAfter(LocalDateTime.now())) {
                lastBooking = booking;
                break;
            }
            nextBooking = booking;
        }
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.isAvailable())
                .description(item.getDescription())
                .comments(item.getComments().stream().map(CommentMapper::toCommentDto).collect(Collectors.toSet()))
                .lastBooking(lastBooking != null ? toShortBookingDto(lastBooking) : null)
                .nextBooking(nextBooking != null ? toShortBookingDto(nextBooking) : null)
                .build();
    }
}