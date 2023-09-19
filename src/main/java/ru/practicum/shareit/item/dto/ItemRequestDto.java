package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemRequestDto {

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 1, max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @NotNull(message = "Статус вещи должен быть Available")
    private Boolean available;

    private Long requestId;
}
