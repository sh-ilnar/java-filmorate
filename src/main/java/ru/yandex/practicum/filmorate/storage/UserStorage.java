package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    Map<Integer, User> getUsers();

    Map<Integer, User> getUsersByIds(Collection<Integer> ids);

    User getUserById(Integer id);

    User addUser(User user);

    User updateUser(User updatedUser);

    void deleteAllUsers();

    void deleteUserById(Integer id);

    Boolean isExistUser(Integer id);

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    Collection<User> getFriends(Integer userId);

    Collection<User> getCommonFriends(Integer userId, Integer otherId);
}
