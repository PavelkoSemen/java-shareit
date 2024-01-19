package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShortBookingDto {
    private long id;
    private long bookerId;
}