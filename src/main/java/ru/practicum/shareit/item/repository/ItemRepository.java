package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    List<Item> findAll();

    List<Item> findItemByOwnerId(long ownerId);

    // Оставил как пример, смориться не очень хорошо, довольно смешно, но можно писать и так
    List<Item> findItemByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase(String name, String description);

    @Query("SELECT i FROM Item i WHERE i.available = true " +
            "AND (upper(i.name) LIKE %:filter% OR upper(i.description) LIKE %:filter%)" +
            "ORDER BY i.id")
    List<Item> searchByFilter(@Param("filter") String filter);


    Optional<Item> findById(Long aLong);

    Item save(Item entity);

    void deleteById(Long id);

    void deleteAll();
}