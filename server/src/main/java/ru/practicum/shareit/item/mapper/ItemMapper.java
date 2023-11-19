package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item itemRequestDtoToItem(ItemRequestDto item) {
        Item createItem = new Item();
        createItem.setName(item.getName());
        createItem.setDescription(item.getDescription());
        createItem.setAvailable(item.getAvailable());
        return createItem;
    }


    public static ItemResponseDto itemToItemResponseDto(Long userId, Item item, List<Comment> commentList,
                                                        List<Booking> lastBookingList, List<Booking> nextBookingList) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        if (item.getItemRequest() != null) {
            itemResponseDto.setRequestId(item.getItemRequest().getId());
        }

        List<CommentResponseDto> commentResponseDtoList = commentList.stream()
                .map(CommentMapper::commentToCommentResponse)
                .collect(Collectors.toList());
        itemResponseDto.setComments(commentResponseDtoList);

        if (userId.equals(item.getOwner().getId())) {

            if (!lastBookingList.isEmpty()) {
                Booking lastBooking = lastBookingList.get(0);
                User user = lastBooking.getBooker();
                User booker = new User();
                booker.setId(user.getId());

                BookingForItemDto lastBookingDto = new BookingForItemDto();
                lastBookingDto.setId(lastBooking.getId());
                lastBookingDto.setStart(lastBooking.getStart());
                lastBookingDto.setEnd(lastBooking.getEnd());
                lastBookingDto.setBookerId(lastBooking.getBooker().getId());

                itemResponseDto.setLastBooking(lastBookingDto);
            }

            if (!nextBookingList.isEmpty()) {
                Booking nextBooking = nextBookingList.get(0);
                User user = nextBooking.getBooker();
                User booker = new User();
                booker.setId(user.getId());

                BookingForItemDto nextBookingDto = new BookingForItemDto();

                nextBookingDto.setId(nextBooking.getId());
                nextBookingDto.setStart(nextBooking.getStart());
                nextBookingDto.setEnd(nextBooking.getEnd());
                nextBookingDto.setBookerId(nextBooking.getBooker().getId());

                itemResponseDto.setNextBooking(nextBookingDto);
            }
        }
        return itemResponseDto;
    }

    public static ItemForItemRequestDto itemToItemForItemRequestDto(Item item) {
        ItemForItemRequestDto itemForItemRequestDto = new ItemForItemRequestDto();
        itemForItemRequestDto.setId(item.getId());
        itemForItemRequestDto.setName(item.getName());
        itemForItemRequestDto.setDescription(item.getDescription());
        itemForItemRequestDto.setAvailable(item.getAvailable());
        itemForItemRequestDto.setRequestId(item.getItemRequest().getId());
        return itemForItemRequestDto;
    }
}

