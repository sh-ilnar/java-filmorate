package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.jdbc.MpaJdbcStorage;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Import(MpaJdbcStorage.class)
public class MpaJdbcStorageTest {

    @Autowired
    private MpaJdbcStorage mpaStorage;

    @Test
    void testFindGenreById() {
        Mpa mpa = mpaStorage.getMpaById(1);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1);

        Assertions.assertEquals("G", mpa.getName(), "Ожидается другое наименование");
    }

    @Test
    void testGetAllUsers() {
        Map<Integer, Mpa> mpas = mpaStorage.getMpas();

        Assertions.assertEquals("G", mpas.get(1).getName(), "Ожидается другое наименование жанра");
        Assertions.assertEquals(5, mpas.size(), "Ожидается другое количество рейтингов");
    }
}
