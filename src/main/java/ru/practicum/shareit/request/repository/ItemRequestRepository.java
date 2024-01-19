package ru.practicum.shareit.request.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends CrudRepository<ItemRequest, Long> {

}