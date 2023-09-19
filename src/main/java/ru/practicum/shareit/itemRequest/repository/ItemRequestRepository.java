package ru.practicum.shareit.itemRequest.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id = :userId ORDER BY ir.created DESC")
    List<ItemRequest> findAllByUserIdOrderByCreatedDesc(Long userId, Pageable page);


    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id != :userId ORDER BY ir.created DESC")
    List<ItemRequest> findAllByCreatedDesc(Long userId, Pageable page);

}
