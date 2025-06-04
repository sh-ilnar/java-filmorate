package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.jdbc.rowMapper.MpaRowMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MpaJdbcStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper rowMapper;

    @Override
    public Map<Integer, Mpa> getMpas() {
        String sqlQuery = "SELECT * FROM mpa;";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, rowMapper);

        return mpas.stream()
                .collect(Collectors.toMap(
                        Mpa::getId,
                        mpa -> mpa
                ));
    }

    @Override
    public Mpa getMpaById(Integer id) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = ?;";

        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, rowMapper, id);
        if (mpas.size() != 1) {
            throw new NotFoundException("такой рейтинг MPA не найден " + id);
        }
        return mpas.getFirst();
    }

    @Override
    public Boolean isExistMpa(Integer id) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = ?";

        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, rowMapper, id);
        return !mpas.isEmpty();
    }
}
