package ru.practicum.shareit.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class ErrorMessage {
    private final int statusCode;
    private final Date timestamp;
    private final String error;
    private final String description;

}