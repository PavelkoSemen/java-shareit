package ru.practicum.shareit.booking.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends CrudRepository<Booking, Long> {
    Booking save(Booking entity);

    Optional<Booking> findById(Long aLong);

    List<Booking> findAll();

    void delete(Booking entity);

    void deleteAll();
}