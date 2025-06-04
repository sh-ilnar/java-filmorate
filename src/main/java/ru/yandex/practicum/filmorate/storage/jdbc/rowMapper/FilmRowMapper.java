package ru.yandex.practicum.filmorate.storage.jdbc.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setDuration(resultSet.getInt("duration"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());

        if (resultSet.getObject("mpa_id") != null) {
            Mpa mpa = new Mpa();
            mpa.setId(resultSet.getInt("mpa_id"));
            mpa.setName(resultSet.getString("mpa_name"));
            mpa.setDescription(resultSet.getString("mpa_description"));
            film.setMpa(mpa);
        } else {
            film.setMpa(null);
        }

        film.setGenres(new LinkedHashSet<>());
        film.setLikes(new LinkedHashSet<>());

        return film;
    }
}
