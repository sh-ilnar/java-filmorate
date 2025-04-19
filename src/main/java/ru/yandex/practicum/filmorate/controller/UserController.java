package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Получен запрос на получение всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(
            @PathVariable("userId") Integer userId
    ) {
        log.info("Получен запрос на получение пользователя с идентификатором: {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        log.info("Получен запрос на обновление пользователя: {}", updatedUser);

        if (updatedUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        return userService.updateUser(updatedUser);
    }

    @DeleteMapping
    public void deleteAll() {
        log.info("Получен запрос на удаление всех пользователей");
        userService.deleteAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable("userId") Integer userId) {
        log.info("Получен запрос на удаление пользователя с идентификатором" + userId);
        userService.deleteUserById(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(
            @PathVariable("userId") Integer userId,
            @PathVariable("friendId") Integer friendId
    ) {
        log.info("Получен запрос на добавления пользователя " + friendId + " в друзья к " + userId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable("userId") Integer userId,
            @PathVariable("friendId") Integer friendId
    ) {
        log.info("Получен запрос на удаление пользователя " + friendId + " в друзья к " + userId);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<User> getFriends(
            @PathVariable("userId") Integer userId
    ) {
        log.info("Получен запрос на получение списка друзей по пользователю " + userId);
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(
            @PathVariable("userId") Integer userId,
            @PathVariable("otherId") Integer otherId
    ) {
        log.info("Получен запрос на получение общего списка друзей");
        return userService.getCommonFriends(userId, otherId);
    }
}
