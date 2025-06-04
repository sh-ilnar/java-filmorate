package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.jdbc.FilmJdbcStorage;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Import(FilmJdbcStorage.class)
public class FilmJdbcStorageTest {

    @Autowired
    private FilmJdbcStorage filmStorage;

    @Test
    void testFindFilmById() {

        Film film = filmStorage.getFilmById(1);

        assertThat(film).isNotNull();
        assertThat(film.getId()).isEqualTo(1);

        Assertions.assertEquals("Матрица", film.getName(), "Ожидается другое наименование");
    }

    @Test
    void testGetFilmByIdNotFound() {
        assertThatThrownBy(() -> filmStorage.getFilmById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("такой фильм не найден");
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setId(2);
        film.setName("updated name");
        film.setDescription("updated descriptiom");
        film.setReleaseDate(LocalDate.of(1999, 1, 10));
        film.setDuration(99);

        filmStorage.updateFilm(film);

        Film updatedFilm = filmStorage.getFilmById(film.getId());

        Assertions.assertEquals(film.getName(), updatedFilm.getName(), "Ожидается другое наименование");
        Assertions.assertEquals(film.getDescription(), updatedFilm.getDescription(), "Ожидается другое описание");
    }

    @Test
    void testGetAllFilms() {

        Film film = new Film();
        film.setName("updated name");
        film.setDescription("updated descriptiom");
        film.setReleaseDate(LocalDate.of(1999, 1, 10));
        film.setDuration(99);

        Film addedFilm = filmStorage.addFilm(film);
        Map<Integer, Film> films = filmStorage.getFilms();

        Assertions.assertEquals(film.getName(), films.get(addedFilm.getId()).getName(), "Ожидается другой фильм");
    }

    @Test
    void testIsExistFilm() {
        assertThat(filmStorage.isExistFilm(1)).isTrue();
        assertThat(filmStorage.isExistFilm(999)).isFalse();
    }
}
