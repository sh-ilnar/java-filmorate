package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class User {

    Integer id;

    @NotNull(message = "email должен быть указан")
    @Email(message = "указан не корректный email")
    String email;

    @NotNull(message = "login должен быть указан")
    @Pattern(regexp = "\\S+", message = "login не может содержать пробелы")
    String login;

    @Nullable
    String name;

    @PastOrPresent(message = "указана не корректная дата рождения")
    LocalDate birthday;

    @JsonIgnore
    Set<Integer> friends;
}