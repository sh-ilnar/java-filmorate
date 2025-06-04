package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.jdbc.GenreJdbcStorage;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Import(GenreJdbcStorage.class)
public class GenreJdbcStorageTest {

    @Autowired
    private GenreJdbcStorage genreStorage;

    @Test
    void testFindGenreById() {
        Genre genre = genreStorage.getGenreById(1);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);

        Assertions.assertEquals("Комедия", genre.getName(), "Ожидается другое наименование жанра");
    }

    @Test
    void testGetAllUsers() {
        Map<Integer, Genre> genres = genreStorage.getGenres();

        Assertions.assertEquals("Комедия", genres.get(1).getName(), "Ожидается другое наименование жанра");
        Assertions.assertEquals(6, genres.size(), "Ожидается другое количество жанров");
    }
}
