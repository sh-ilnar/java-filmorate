package ru.yandex.practicum.filmorate.storage.memory;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortOrder;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

//@Component
//@Qualifier("inMemoryFilmStorage")
@Deprecated
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Collection<Film> getFilmsWithPagination(
            SortOrder sortOrder,
            int from,
            int size) {
        return films.values().stream()
                .sorted(getLikesComparator(sortOrder))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(Integer id) {
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        Integer newId = getNextId();
        film.setId(newId);

        films.put(film.getId(), film);
        return films.get(newId);
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        Film film = films.get(updatedFilm.getId());
        film.setName(updatedFilm.getName());
        film.setDescription(updatedFilm.getDescription());
        film.setReleaseDate(updatedFilm.getReleaseDate());
        film.setDuration(updatedFilm.getDuration());
        return film;
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
    }

    @Override
    public Boolean isExistFilm(Integer id) {
        return films.containsKey(id);
    }

    @Override
    public void putLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);

        Set<Integer> filmLikes = film.getLikes();
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
            film.setLikes(filmLikes);
        }
        film.getLikes().add(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);
        Set<Integer> filmLikes = film.getLikes();
        if (filmLikes == null) {
            return;
        }

        film.getLikes().remove(userId);
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private static Comparator<Film> getLikesComparator(SortOrder sortOrder) {

        Comparator<Film> comparator = Comparator.comparing(Film::getLikesSize);

        if (sortOrder.equals(SortOrder.DESCENDING)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}
