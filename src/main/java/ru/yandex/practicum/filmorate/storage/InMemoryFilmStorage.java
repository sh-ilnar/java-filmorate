package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    public Map<Integer, Film> getFilms() {
        return films;
    }

    public Film getFilmById(Integer id) {
        return films.get(id);
    }

    public Film addFilm(Film newFilm) {
        Integer newId = getNextId();
        newFilm.setId(newId);

        films.put(newFilm.getId(), newFilm);
        return films.get(newId);
    }

    public Film updateFilm(Film updatedFilm) {
        Film film = films.get(updatedFilm.getId());
        film.setName(updatedFilm.getName());
        film.setDescription(updatedFilm.getDescription());
        film.setReleaseDate(updatedFilm.getReleaseDate());
        film.setDuration(updatedFilm.getDuration());
        return film;
    }

    public void deleteAllFilms() {
        films.clear();
    }

    public Boolean isExistFilm(Integer id) {
        return films.containsKey(id);
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
