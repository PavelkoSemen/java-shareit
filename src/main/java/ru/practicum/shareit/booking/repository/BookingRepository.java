package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Не понимаю зачем в данном случае делать FetchType.LAZY в Booking, если по сути, практически каждый метод
 * BookingService использует всю структуру.
 * Почему я сделал Iterable<Booking> просто не захотел завязываться на реализацию List. В принципе можно было сделать
 * через него. Нет большой разницы.
 */
public interface BookingRepository extends CrudRepository<Booking, Long> {
    @Query(" SELECT b " +
            "  FROM Booking b " +
            "  JOIN FETCH b.item i " +
            "  JOIN FETCH i.owner o " +
            " WHERE  b.id = ?1 and o.id = ?2")
    Optional<Booking> findByBookingIdWithOwnerId(Long bookerId, Long ownerId);

    @Query(" SELECT b " +
            "  FROM Booking b " +
            "  JOIN FETCH b.item i " +
            "  JOIN FETCH i.owner o " +
            " WHERE  b.id = ?1 and (o.id = ?2 or b.booker.id = ?2)")
    Optional<Booking> findByOwnerIdOrUser(Long bookerId, Long userId);

    @Query(" SELECT b " +
            "  FROM Booking b " +
            "  JOIN FETCH b.item i " +
            "  JOIN FETCH i.owner o " +
            " WHERE o.id = ?1")
    List<Booking> findByOwnerId(Long ownerId);

    @Query(" SELECT b " +
            "  FROM Booking b " +
            "  JOIN FETCH b.item i " +
            "  JOIN FETCH i.owner o " +
            " WHERE  b.booker.id = ?1")
    List<Booking> findByUserId(Long userId);

    @Query(" SELECT b " +
            "  FROM Booking b " +
            "  JOIN FETCH b.item i " +
            "  JOIN FETCH i.owner o " +
            " WHERE  b.item.id = ?1 and b.status = ?2" +
            " ORDER BY b.end DESC")
    List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus bookingStatus);

    @Query(" SELECT b " +
            "  FROM Booking b " +
            "  JOIN FETCH b.item i " +
            "  JOIN FETCH i.owner o " +
            " WHERE  b.item.id in ?1" +
            " ORDER BY b.end DESC")
    List<Booking> findByItemsId(List<Long> itemsId);


    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long ItemId, LocalDateTime bookerTime);
}