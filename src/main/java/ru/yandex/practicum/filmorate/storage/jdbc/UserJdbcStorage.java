package ru.yandex.practicum.filmorate.storage.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.jdbc.rowMapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserJdbcStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final UserRowMapper rowMapper;

    @Override
    public Map<Integer, User> getUsers() {
        String sqlQuery = "SELECT * from users;";
        List<User> users = jdbcTemplate.query(sqlQuery, rowMapper);

        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));
    }

    @Override
    public Map<Integer, User> getUsersByIds(Collection<Integer> ids) {
        String sqlQuery = "SELECT * FROM users WHERE id IN (:userIds);";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("userIds", ids);

        List<User> users = namedJdbcTemplate.query(sqlQuery, parameters, (resultSet, rowNum) -> {
            User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setEmail(resultSet.getString("email"));
            user.setLogin(resultSet.getString("login"));
            user.setName(resultSet.getString("name"));
            user.setBirthday(resultSet.getDate("birthday").toLocalDate());
            return user;
        });

        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));
    }

    @Override
    public User getUserById(Integer id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";

        List<User> films = jdbcTemplate.query(sqlQuery, rowMapper, id);
        if (films.size() != 1) {
            throw new NotFoundException("такой пользователь не найден " + id);
        }

        return films.getFirst();
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "INSERT INTO users (login, email, name, birthday) " +
                "VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"id"});
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return getUserById(user.getId());
    }

    @Override
    public User updateUser(User updatedUser) {
        String sqlQuery = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                updatedUser.getLogin(),
                updatedUser.getEmail(),
                updatedUser.getName(),
                updatedUser.getBirthday(),
                updatedUser.getId()
        );

        return getUserById(updatedUser.getId());
    }

    @Override
    public void deleteAllUsers() {
        String sqlQuery = "DELETE FROM users;";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public void deleteUserById(Integer id) {
        String sqlQuery = "DELETE FROM users WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Boolean isExistUser(Integer id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?;";
        List<User> users = jdbcTemplate.query(sqlQuery, rowMapper, id);
        return !users.isEmpty();
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sqlQuery = "INSERT INTO friend_requests (user_id, friend_id) " +
                "VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sqlQuery = "DELETE FROM friend_requests WHERE user_id = ? AND friend_id = ? ";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Integer userId) {
        String sqlQuery = "SELECT u.* FROM users u " +
                "JOIN friend_requests fr ON u.id = fr.friend_id " +
                "WHERE (fr.user_id = ?) ";
        return jdbcTemplate.query(sqlQuery, rowMapper, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        String sqlQuery = "SELECT u.* FROM users u " +
                "JOIN friend_requests fr1 ON u.id = fr1.friend_id " +
                "JOIN friend_requests fr2 ON u.id = fr2.friend_id " +
                "WHERE fr1.user_id = ? AND fr2.user_id = ?;";
        return jdbcTemplate.query(sqlQuery, rowMapper, userId, otherId);
    }
}
