package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.jdbc.rowMapper.GenreRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreJdbcStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final GenreRowMapper rowMapper;


    @Override
    public Map<Integer, Genre> getGenres() {
        String sqlQuery = "SELECT * FROM genres;";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, rowMapper);

        return genres.stream()
                .collect(Collectors.toMap(
                        Genre::getId,
                        genre -> genre
                ));
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?;";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, rowMapper, id);
        if (genres.size() != 1) {
            throw new NotFoundException("такой жанр не найден " + id);
        }
        return genres.getFirst();
    }

    @Override
    public Boolean isExistGenre(Integer id) {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, rowMapper, id);
        return !genres.isEmpty();
    }

    public void validateGenreIdsExist(Set<Integer> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        String sqlQuery = "SELECT COUNT(*) FROM genres WHERE id IN (:ids)";
        Map<String, Object> params = Map.of("ids", genreIds);

        int foundCount = namedJdbcTemplate.queryForObject(sqlQuery, params, Integer.class);

        if (foundCount != genreIds.size()) {
            throw new NotFoundException("Один или несколько жанров не найдены в базе данных");
        }
    }
}
