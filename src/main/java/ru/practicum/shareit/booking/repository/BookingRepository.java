package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "ORDER BY b.end DESC")
    List<Booking> findNearestBookingBeforeCurrentTimeForItemId(Long itemId);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC ")
    List<Booking> findNextBookingAfterCurrentTimeForItemId(Long itemId);


    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findPastBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> findWaitingBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> findRejectedBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findByOwner(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= CURRENT_TIMESTAMP AND " +
            "b.end >= CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwner(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwner(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwner(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> findWaitingBookingsByOwner(Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> findRejectedBookingsByOwner(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end <= CURRENT_TIMESTAMP")
    List<Booking> findPastBookingsByBookerIdAndItemId(Long bookerId, Long itemId);
}


