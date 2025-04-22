package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortOrder;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return filmService.getAllFilms();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма");
        return filmService.addFilm(film);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Film update(@Valid @RequestBody Film updatedFilm) {
        log.info("Получен запрос на обновление фильма");

        if (updatedFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        return filmService.updateFilm(updatedFilm);
    }

    @DeleteMapping
    public void deleteAll() {
        log.info("Получен запрос на удаление всех фильмов");
        filmService.deleteAllFilms();
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void putLike(
            @PathVariable("filmId") Integer filmId,
            @PathVariable("userId") Integer userId
    ) {
        log.info("Получен запрос на лайк фильму от пользователя");
        filmService.putLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(
            @PathVariable("filmId") Integer filmId,
            @PathVariable("userId") Integer userId
    ) {
        log.info("Получен запрос на удаление лайка фильму от пользователя");
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> findFilmsByParams(
            @RequestParam(defaultValue = "10") Integer count
    ) {
        log.info("Получен запрос на получение популярных фильмов.");
        return filmService.getPopularFilms(SortOrder.DESCENDING, 0, count);
    }
}
