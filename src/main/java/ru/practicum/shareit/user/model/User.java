package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class User {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    private String email;

    LocalDate registryDate;
}
