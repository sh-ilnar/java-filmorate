package ru.yandex.practicum.filmorate.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {

    private Integer id;

    @NotNull(message = "email должен быть указан")
    @Email(message = "указан не корректный email")
    private String email;

    @NotNull(message = "login должен быть указан")
    @Pattern(regexp = "\\S+", message = "login не может содержать пробелы")
    private String login;

    @Nullable
    private String name;

    @PastOrPresent(message = "указана не корректная дата рождения")
    private LocalDate birthday;
}