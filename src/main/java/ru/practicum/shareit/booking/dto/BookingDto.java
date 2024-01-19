package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    @NotNull
    private long itemId;
    @NotNull(message = "Start booking date cannot be empty")
    @FutureOrPresent(message = "Start booking date it cannot be less current date")
    private LocalDateTime start;
    @NotNull(message = "End booking date cannot be empty")
    @FutureOrPresent(message = "End booking date it cannot be less current date")
    private LocalDateTime end;
}