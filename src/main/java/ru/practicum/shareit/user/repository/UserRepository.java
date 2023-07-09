package ru.practicum.shareit.user.repository;

import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    User save(User entity);

    Optional<User> findById(Long aLong);

    List<User> findAll();

    void delete(User entity);

    void deleteAll();
}