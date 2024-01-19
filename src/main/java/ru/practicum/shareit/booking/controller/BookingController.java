package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.FullBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<FullBookingDto> getBookingUserWithState(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                                        @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getUserBookingWithState(userId, state);
    }

    @GetMapping("owner")
    public List<FullBookingDto> getBookingOwnerWithState(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookingWithState(userId, state);
    }

    @GetMapping("{bookingId}")
    public FullBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                     @PathVariable long bookingId) {
        return bookingService.getBookingByOwnerOrUser(userId, bookingId);
    }

    @PostMapping
    public FullBookingDto createBooking(@RequestHeader("X-Sharer-User-Id") @NotNull long userId,
                                        @RequestBody @Valid BookingDto bookingDto) {

        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("{bookingId}")
    public FullBookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") @NotNull long ownerId,
                                         @PathVariable Long bookingId,
                                         @RequestParam(name = "approved") boolean isApproved) {
        return bookingService.approveBooking(ownerId, bookingId, isApproved);
    }
}