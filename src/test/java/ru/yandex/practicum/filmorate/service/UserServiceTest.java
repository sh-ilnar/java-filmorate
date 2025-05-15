package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void setUserService() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    @Test
    void givenCreatedUsers_whenGetUsers_thenReturnUsers() {
        User user1 = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );
        User user2 = new User(
                2,
                "test2@test.com",
                "testLogin2",
                "name2",
                LocalDate.of(1999, 1, 10),
                null
        );

        userStorage.addUser(user1);
        userStorage.addUser(user2);

        Collection<User> users = userService.getUsers();

        assertEquals(2, users.size());
        assertTrue(users.containsAll(Set.of(user1, user2)));
    }

    @Test
    void givenCreatedUser_whenGetUserById_thenReturnUser() {
        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );
        userStorage.addUser(user);
        User returnUser = userService.getUserById(user.getId());
        assertEquals(user, returnUser);
    }

    @Test
    void notExistingId_whenGetUserById_thenThrowNotFoundException() {
        Assertions.assertThrows(NotFoundException.class, () ->
                userService.getUserById(100));
    }

    @Test
    void whenAddNewUser_thenReturnAddedUser() {
        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );
        User newUser = userService.addUser(user);

        assertEquals(user, newUser);
    }

    @Test
    void givenCreatedUser_whenUpdateUser_thenReturnUpdatedUser() {
        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );
        userStorage.addUser(user);

        User changedUser = new User(
                1,
                "test2@test.com",
                "testLogin2",
                "name2",
                LocalDate.of(1967, 3, 25),
                null
        );
        User updatedUser = userService.updateUser(changedUser);

        assertEquals(changedUser, updatedUser);
    }

    @Test
    void givenCreatedUser_whenDeleteUserById_thenDeletedUser() {
        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );
        userStorage.addUser(user);

        userService.deleteUserById(user.getId());

        assertFalse(userStorage.isExistUser(user.getId()));
    }

    @Test
    void givenCreatedUserFriend_whenPutFriend_thenFriendIsAdded() {
        User user = new User(
                1,
                "test@test.com",
                "testLogin",
                "name1",
                LocalDate.of(1967, 3, 25),
                null
        );
        User friend = new User(
                2,
                "test2@test.com",
                "testLogin2",
                "name2",
                LocalDate.of(1999, 1, 10),
                null
        );
        userStorage.addUser(user);
        userStorage.addUser(friend);

        userService.addFriend(user.getId(), friend.getId());

        assertTrue(user.getFriends().contains(friend.getId()));
    }
}
