package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genre> getAllGenres() {
        return genreStorage.getGenres().values();
    }

    public Genre getGenreById(Integer id) {
        if (!genreStorage.isExistGenre(id)) {
            throw new NotFoundException("Жанр с указанным идентификатором не найден");
        }
        return genreStorage.getGenreById(id);
    }
}
