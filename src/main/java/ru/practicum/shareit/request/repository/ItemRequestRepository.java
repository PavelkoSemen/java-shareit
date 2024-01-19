package ru.practicum.shareit.request.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends CrudRepository<ItemRequest, Long> {

}