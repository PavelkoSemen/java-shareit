package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.EntityLockedException;
import ru.practicum.shareit.error.EntityNotFoundException;
import ru.practicum.shareit.error.RequestParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<FullBookingDto> getUserBookingWithState(long userId, String state) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователя с id " + userId + " не существует"));

        return this.filterBooking(bookingRepository.findByUserId(userId), state);
    }

    @Override
    public List<FullBookingDto> getOwnerBookingWithState(long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() ->
                new EntityNotFoundException("Пользователя с id " + ownerId + " не существует"));

        return this.filterBooking(bookingRepository.findByOwnerId(ownerId), state);
    }

    @Override
    public FullBookingDto getBookingByOwnerOrUser(Long userId, Long bookingId) {
        final Booking booking = bookingRepository.findByOwnerIdOrUser(bookingId, userId).orElseThrow(() ->
                new EntityNotFoundException("Бронирования с id " + bookingId + " не существует"));

        return toBookingResponseDto(booking);
    }

    @Override
    @Transactional
    public FullBookingDto createBooking(Long userId, BookingDto bookingDto) {
        log.info("Создание бронирования {} у пользователя c id {}", bookingDto, userId);
        final User bookingUser = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователя с id " + userId + " не существует"));
        final long itemId = bookingDto.getItemId();
        final Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Предмета с id " + itemId + " не существует"));

        if (!item.isAvailable()) {
            throw new EntityLockedException("Предмета с id " + itemId + " забронирован");
        }

        if (item.getOwner().getId() == userId) {
            throw new EntityNotFoundException("Непонятно почему я не могу забронировать собственный предмет");
        }

        this.checkBookedTime(bookingDto);

        final Booking booking = toBooking(bookingDto);
        booking.setBooker(bookingUser);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public FullBookingDto approveBooking(Long ownerId, Long bookingId, boolean isApproved) {
        final Booking booking = bookingRepository.findByBookingIdWithOwnerId(bookingId, ownerId).orElseThrow(() ->
                new EntityNotFoundException("Запроса с id " + bookingId + " для подтверждения пользователем " +
                        ownerId + " не существует"));

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new RequestParameterException("Запрос " + bookingId + " уже потвержден");
        }

        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return toBookingResponseDto(booking);
    }

    private void checkBookedTime(BookingDto bookingDto) {
        final LocalDateTime startDate = bookingDto.getStart();
        final LocalDateTime endDate = bookingDto.getEnd();

        if ((startDate.isAfter(endDate) || startDate.equals(endDate))) {
            throw new RequestParameterException("Неверная дата начала " + startDate +
                    " и конца бронирования " + endDate);
        }

        final boolean isBookedTime = bookingRepository.findByItemIdAndStatus(bookingDto.getItemId()
                        , BookingStatus.APPROVED)
                .stream()
                .anyMatch(b -> ((b.getStart().isBefore(startDate)
                        && b.getEnd().isAfter(startDate))
                        || (b.getStart().isAfter(startDate)
                        && b.getStart().isBefore(endDate))
                        || (b.getStart().equals(startDate)
                        && b.getEnd().equals(endDate))
                ));

        if (isBookedTime) {
            throw new EntityLockedException("Время: " + startDate + "-" + endDate + " - уже занято ");
        }
    }

    /**
     * Реализовал через стримы, конечно можно было сделать через bookingRepository, особой разницы нет.
     *
     * Не совсем понял почему у CURRENT друга сортировка, какое-то синтетическое усложнение.
     * @param bookings - бронирование
     * @param state - состояние
     * @return - List<FullBookingDto>
     */
    private List<FullBookingDto> filterBooking(List<Booking> bookings, String state) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new RequestParameterException("Unknown state: " + state);
        }
        Stream<Booking> bookingStream = bookings.stream()
                .sorted(Comparator.comparingLong(Booking::getId).reversed());

        switch (bookingState) {
            case ALL:
                break;
            case PAST:
                bookingStream = bookingStream
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()));
                break;
            case CURRENT:
                bookingStream = bookingStream
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparingLong(Booking::getId));
                break;
            case FUTURE:
                bookingStream = bookingStream
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()));
                break;
            case WAITING:
                bookingStream = bookingStream
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING));
                break;
            case REJECTED:
                bookingStream = bookingStream
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED));
                break;
        }
        return bookingStream
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

}