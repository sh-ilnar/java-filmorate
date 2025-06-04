package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers().values();
    }

    public User getUserById(Integer id) {
        if (!userStorage.isExistUser(id)) {
            throw new NotFoundException("Пользователь с указанным идентификатором не найден");
        }
        return userStorage.getUserById(id);
    }

    public User addUser(User newUser) {
        return userStorage.addUser(newUser);
    }

    public User updateUser(User updatedUser) {
        if (!userStorage.isExistUser(updatedUser.getId())) {
            throw new NotFoundException("Пользователь с указанным идентификатором не найден");
        }
        return userStorage.updateUser(updatedUser);
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    public void deleteUserById(Integer id) {
        if (!userStorage.isExistUser(id)) {
            throw new NotFoundException("Пользователь с указанным идентификатором не найден");
        }
        userStorage.deleteUserById(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (!userStorage.isExistUser(userId)) {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        if (!userStorage.isExistUser(friendId)) {
            throw new NotFoundException("Пользователь с идентификатором " + friendId + " не найден");
        }

        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (!userStorage.isExistUser(userId)) {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        if (!userStorage.isExistUser(friendId)) {
            throw new NotFoundException("Пользователь с идентификатором " + friendId + " не найден");
        }

        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriends(Integer userId) {
        if (!userStorage.isExistUser(userId)) {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        return userStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        if (!userStorage.isExistUser(userId)) {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        if (!userStorage.isExistUser(otherId)) {
            throw new NotFoundException("Пользователь с идентификатором " + otherId + " не найден");
        }
        return userStorage.getCommonFriends(userId, otherId);
    }
}
