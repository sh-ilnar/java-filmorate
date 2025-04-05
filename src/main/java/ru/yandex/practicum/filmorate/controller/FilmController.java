package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Получен запрос на получение всех фильмов");
        return films.values();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма");

        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(minReleaseDate)) {
            log.error("не корректная дата релиза");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление фильма");

        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
            if (newFilm.getReleaseDate().isBefore(minReleaseDate)) {
                log.error("не корректная дата релиза");
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @DeleteMapping
    public void deleteAll() {
        films.clear();
    }
}
