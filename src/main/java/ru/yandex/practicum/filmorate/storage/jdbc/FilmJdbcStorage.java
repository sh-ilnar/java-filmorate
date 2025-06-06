package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.SortOrder;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.jdbc.rowMapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmJdbcStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final FilmRowMapper rowMapper;
    private final GenreJdbcStorage genreJdbcStorage;
    private final MpaJdbcStorage mpaJdbcStorage;

    @Override
    public Map<Integer, Film> getFilms() {
        String sqlQuery = "SELECT f.*, m.id as mpa_id, m.name as mpa_name, m.description as mpa_description " +
                "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id;";
        List<Film> films = jdbcTemplate.query(sqlQuery, rowMapper);

        loadGenresForFilms(films);
        loadLikesForFilms(films);

        return films.stream()
                .collect(Collectors.toMap(
                        Film::getId,
                        film -> film
                ));
    }

    @Override
    public Collection<Film> getFilmsWithPagination(
            SortOrder sortOrder,
            int from,
            int size) {

        String orderBy;
        if (Objects.requireNonNull(sortOrder) == SortOrder.ASCENDING) {
            orderBy = "ASC";
        } else {
            orderBy = "DESC";
        }

        String sql = "SELECT f.*, m.id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description " +
                "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "ORDER BY (SELECT COUNT(*) FROM likes WHERE film_id = f.id) " + orderBy + " " +
                "LIMIT ? OFFSET ?";

        List<Film> films = jdbcTemplate.query(sql, rowMapper, size, from);

        loadGenresForFilms(films);
        loadLikesForFilms(films);

        return films;
    }

    @Override
    public Film getFilmById(Integer id) {
        String sqlQuery = "SELECT f.*, m.id as mpa_id, m.name as mpa_name, m.description as mpa_description " +
                "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, rowMapper, id);
        if (films.size() != 1) {
            throw new NotFoundException("такой фильм не найден " + id);
        }
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        return films.getFirst();
    }

    @Override
    public Film addFilm(Film film) {

        if (film.getMpa() != null) {
            if (!mpaJdbcStorage.isExistMpa(film.getMpa().getId())) {
                throw new NotFoundException("Рейтинг MPA не найден " + film.getMpa().getId());
            }
        }

        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());

            if (film.getMpa() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        saveFilmGenres(film);

        return getFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {

        if (film.getMpa() != null) {
            if (!mpaJdbcStorage.isExistMpa(film.getMpa().getId())) {
                throw new NotFoundException("Рейтинг MPA не найден " + film.getMpa().getId());
            }
        }

        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                "WHERE id = ?;";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        saveFilmGenres(film);

        return getFilmById(film.getId());
    }

    @Override
    public void deleteAllFilms() {
        String sqlQuery = "DELETE FROM films;";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public Boolean isExistFilm(Integer id) {
        String sqlQuery = "SELECT f.*, m.id as mpa_id, m.name as mpa_name, m.description as mpa_description " +
                "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, rowMapper, id);
        return !films.isEmpty();
    }

    @Override
    public void putLike(Integer filmId, Integer userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) " +
                "VALUES (?, ?);";

        jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";

        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    private void loadGenresForFilms(List<Film> films) {
        if (CollectionUtils.isEmpty(films) ) {
            return;
        }

        String sqlQuery = "SELECT fg.film_id, g.id as genre_id, g.name as genre_name " +
                "FROM film_genres fg JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (:filmIds);";

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("filmIds", filmIds);

        namedJdbcTemplate.query(sqlQuery, parameters, rs -> {
            int filmId = rs.getInt("film_id");
            Film film = filmMap.get(filmId);

            if (film != null) {
                Genre genre = new Genre();
                genre.setId(rs.getInt("genre_id"));
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }
        });
    }

    private void saveFilmGenres(Film film) {
        final Integer filmId = film.getId();
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);

        final Set<Genre> genres = film.getGenres();
        if (CollectionUtils.isEmpty(genres)) {
            return;
        }

        final Set<Integer> genreIds = genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        genreJdbcStorage.validateGenreIdsExist(genreIds);

        final ArrayList<Genre> genreList = new ArrayList<>(genres);
        String genreInsertSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(genreInsertSql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, filmId);
                        ps.setInt(2, genreList.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genreList.size();
                    }
                }
        );
    }

    private void loadLikesForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        String sqlQuery = "SELECT film_id, user_id " +
                "FROM likes  " +
                "WHERE film_id IN (:filmIds);";

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("filmIds", filmIds);

        namedJdbcTemplate.query(sqlQuery, parameters, rs -> {
            int filmId = rs.getInt("film_id");
            Film film = filmMap.get(filmId);

            if (film != null) {
                Set<Integer> likes = new HashSet<>();
                likes.add(rs.getInt("user_id"));
                film.setLikes(likes);
            }
        });
    }
}
