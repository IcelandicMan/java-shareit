package ru.practicum.shareit.itemrequest.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ItemRequestRequestedDto {

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 1, max = 500, message = "Максимальная длина описания - 500 символов")
    private String description;
}
