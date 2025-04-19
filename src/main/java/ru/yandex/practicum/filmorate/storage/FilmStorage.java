package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getFilms();

    Film getFilmById(Integer id);

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    void deleteAllFilms();

    Boolean isExistFilm(Integer id);
}
