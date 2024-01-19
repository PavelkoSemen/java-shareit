package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;

import java.util.List;

public interface BookingService {
    List<FullBookingDto> getUserBookingWithState(long userId, String state);

    List<FullBookingDto> getOwnerBookingWithState(long ownerId, String state);

    FullBookingDto getBookingByOwnerOrUser(Long userId, Long bookingId);

    FullBookingDto createBooking(Long userId, BookingDto bookingDto);

    FullBookingDto approveBooking(Long ownerId, Long bookingId, boolean isApproved);

}