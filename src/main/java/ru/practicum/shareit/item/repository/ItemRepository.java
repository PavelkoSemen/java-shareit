package ru.practicum.shareit.item.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    Item save(Item entity);

    Optional<Item> findById(Long aLong);

    List<Item> findAll();

    void delete(Item entity);

    void deleteAll();
}