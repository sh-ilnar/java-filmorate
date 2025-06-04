package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortOrder;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getFilms();

    Collection<Film> getFilmsWithPagination(SortOrder sortOrder, int from, int size);

    Film getFilmById(Integer id);

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    void deleteAllFilms();

    Boolean isExistFilm(Integer id);

    void putLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);
}
