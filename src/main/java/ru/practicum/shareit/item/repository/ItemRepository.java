package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long> {
    List<Item> findItemByOwnerId(long ownerId);

    @Query("SELECT i FROM Item i WHERE i.available = true " +
            "AND (upper(i.name) LIKE %:filter% OR upper(i.description) LIKE %:filter%)" +
            "ORDER BY i.id")
    List<Item> searchByFilter(@Param("filter") String filter);
}