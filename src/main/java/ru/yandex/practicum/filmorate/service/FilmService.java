package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortOrder;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getFilms().values();
    }

    public Collection<Film> getPopularFilms(
            SortOrder sortOrder,
            int from,
            int size
    ) {
        return filmStorage.getFilmsWithPagination(sortOrder, from, size);
    }

    public Film getFilmById(Integer id) {
        if (!filmStorage.isExistFilm(id)) {
            throw new NotFoundException("Фильм с указанным идентификатором не найден");
        }
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(Film newFilm) {
        validateReleaseDate(newFilm);
        return filmStorage.addFilm(newFilm);
    }

    public Film updateFilm(Film updatedFilm) {
        if (!filmStorage.isExistFilm(updatedFilm.getId())) {
            throw new NotFoundException("Фильм с указанным идентификатором не найден");
        }
        validateReleaseDate(updatedFilm);
        return filmStorage.updateFilm(updatedFilm);
    }

    public void deleteAllFilms() {
        filmStorage.deleteAllFilms();
    }

    public void putLike(Integer filmId, Integer userId) {
        if (!userStorage.isExistUser(userId)) {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        if (!filmStorage.isExistFilm(filmId)) {
            throw new NotFoundException("Фильм с идентификатором " + filmId + " не найден");
        }

        Film film = filmStorage.getFilmById(filmId);
        if (!film.getLikes().contains(userId)) {
            filmStorage.putLike(filmId, userId);
        }
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (!userStorage.isExistUser(userId)) {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        if (!filmStorage.isExistFilm(filmId)) {
            throw new NotFoundException("Фильм с идентификатором " + filmId + " не найден");
        }

        Film film = filmStorage.getFilmById(filmId);
        if (film.getLikes().contains(userId)) {
            filmStorage.deleteLike(filmId, userId);
        }
    }

    private void validateReleaseDate(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
