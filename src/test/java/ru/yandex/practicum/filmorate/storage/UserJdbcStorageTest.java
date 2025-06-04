package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.jdbc.UserJdbcStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@Import(UserJdbcStorage.class)
class UserJdbcStorageTest {

    @Autowired
    private UserJdbcStorage userStorage;

    @Test
    void testFindUserById() {

        User user = userStorage.getUserById(1);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);

        Assertions.assertEquals("user_1", user.getLogin(), "Ожидается другой логин");
        Assertions.assertEquals("Иван Иванов", user.getName(), "Ожидается другое имя");
    }

    @Test
    void testGetUserByIdNotFound() {
        assertThatThrownBy(() -> userStorage.getUserById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("такой пользователь не найден");
    }

    @Test
    void testUpdateUser() {
        User user = new User(
                2,
                "updated@example.com",
                "updatedLogin",
                "Updated Name",
                LocalDate.of(1999, 1, 10),
                new HashSet<>());

        userStorage.updateUser(user);
        User returnedUser = userStorage.getUserById(user.getId());

        Assertions.assertEquals(user.getLogin(), returnedUser.getLogin(), "Ожидается другой логин");
        Assertions.assertEquals(user.getName(), returnedUser.getName(), "Ожидается другое имя");

    }

    @Test
    void testGetAllUsers() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setName("name1");
        user.setBirthday(LocalDate.of(1967, 3, 25));

        User addedUser = userStorage.addUser(user);
        Map<Integer, User> users = userStorage.getUsers();

        Assertions.assertEquals(user.getLogin(), users.get(addedUser.getId()).getLogin(), "Ожидается другой логин");
    }

    @Test
    void testGetUsersByIds() {
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);

        Map<Integer, User> result = userStorage.getUsersByIds(ids);
        assertThat(result).hasSize(2);
        assertThat(result.keySet()).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void testIsExistUser() {
        assertThat(userStorage.isExistUser(1)).isTrue();
        assertThat(userStorage.isExistUser(999)).isFalse();
    }
}
