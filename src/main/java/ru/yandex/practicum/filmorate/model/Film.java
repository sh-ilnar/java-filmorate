package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Data
@AllArgsConstructor
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class Film {

    Integer id;

    @NotBlank(message = "должно быть указано наименование")
    String name;

    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    @PastOrPresent(message = "Указана не корректная дата релиза")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    Integer duration;

    Set<Integer> likes;

    public Integer getLikesSize() {
        if (likes == null) {
            return 0;
        }
        else {
            return likes.size();
        }
    }
}