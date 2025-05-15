package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmServiceTest {
    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;

    @BeforeEach
    void setFilmService() {
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
    }

    @Test
    void givenCreatedFilms_whenGetFilms_thenReturnFilms() {
        Film film1 = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1999, 3, 25),
                100,
                null
        );

        Film film2 = new Film(
                2,
                "asdfsdf",
                "zxcvzxvczxv",
                LocalDate.of(2000, 3, 25),
                100,
                null
        );

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);

        Collection<Film> films = filmService.getAllFilms();

        assertEquals(2, films.size());
        assertTrue(films.containsAll(Set.of(film1, film2)));
    }

    @Test
    void givenCreatedFilm_whenGetFilmById_thenReturnFilm() {
        Film film = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1999, 3, 25),
                100,
                null
        );
        filmStorage.addFilm(film);
        Film returnFilm = filmService.getFilmById(film.getId());

        assertEquals(film, returnFilm);
    }

    @Test
    void notExistingId_whenGetFilmById_thenThrowNotFoundException() {
        Assertions.assertThrows(NotFoundException.class, () ->
                filmService.getFilmById(100));
    }

    @Test
    void whenAddNewFilm_thenReturnAddedFilm() {
        Film film = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1999, 3, 25),
                100,
                null
        );
        Film newFilm = filmService.addFilm(film);

        assertEquals(film, newFilm);
    }

    @Test
    void givenCreatedFilm_whenUpdateFilm_thenReturnUpdatedFilm() {
        Film film = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1999, 3, 25),
                100,
                null
        );
        filmStorage.addFilm(film);

        Film changedFilm = new Film(
                1,
                "asdfas qwerwv",
                "zxcv",
                LocalDate.of(2000, 3, 25),
                150,
                null
        );
        Film updatedFilm = filmService.updateFilm(changedFilm);

        assertEquals(changedFilm, updatedFilm);
    }

    @Test
    void givenCreatedFilmUser_whenPutLike_thenLikeIsPuted() {
        Film film = new Film(
                1,
                "nisi eiusmod",
                "adipisicing",
                LocalDate.of(1999, 3, 25),
                100,
                null
        );
        filmStorage.addFilm(film);

        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );
        userStorage.addUser(user);

        filmService.putLike(film.getId(), user.getId());

        assertTrue(film.getLikes().contains(user.getId()));
    }
}
