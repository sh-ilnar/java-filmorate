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

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        Set<Integer> userFriends = user.getFriends();
        if (userFriends == null) {
            userFriends = new HashSet<>();
            user.setFriends(userFriends);
        }

        Set<Integer> friendFriends = friend.getFriends();
        if (friendFriends == null) {
            friendFriends = new HashSet<>();
            friend.setFriends(friendFriends);
        }

        userFriends.add(friendId);
        friendFriends.add(userId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (!userStorage.isExistUser(userId)) {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        if (!userStorage.isExistUser(friendId)) {
            throw new NotFoundException("Пользователь с идентификатором " + friendId + " не найден");
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();

        if (userFriends == null || friendFriends == null) {
            return;
        }

        userFriends.remove(friendId);
        friendFriends.remove(userId);
    }

    public Collection<User> getFriends(Integer userId) {
        if (!userStorage.isExistUser(userId)) {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        User user = getUserById(userId);
        Collection<Integer> friendsIds = user.getFriends();

        if (friendsIds == null) {
            return Collections.emptyList();
        }
        return userStorage.getUsersByIds(friendsIds).values();
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();

        Set<Integer> userIds = userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toSet());

        return userStorage.getUsersByIds(userIds).values();
    }
}
