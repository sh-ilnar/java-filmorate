package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Map;

public interface GenreStorage {
    Map<Integer, Genre> getGenres();

    Genre getGenreById(Integer id);

    Boolean isExistGenre(Integer id);
}
