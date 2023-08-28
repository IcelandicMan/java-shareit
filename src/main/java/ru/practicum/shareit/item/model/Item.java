package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@Data
public class Item {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private List<ItemRequest> itemRequests = new ArrayList<>();

}
