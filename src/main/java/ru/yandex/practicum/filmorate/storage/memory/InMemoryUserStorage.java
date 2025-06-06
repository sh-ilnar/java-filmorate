package ru.yandex.practicum.filmorate.storage.memory;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

//@Component
//@Qualifier("inMemoryUserStorage")
@Deprecated
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    public Map<Integer, User> getUsers() {
        return users;
    }

    public Map<Integer, User> getUsersByIds(Collection<Integer> ids) {
        return users.entrySet().stream()
                .filter(x -> ids.contains(x.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public User getUserById(Integer id) {
        return users.get(id);
    }

    public User addUser(User newUser) {
        Integer newId = getNextId();
        newUser.setId(newId);

        if (newUser.getName() == null)
            newUser.setName(newUser.getLogin());

        users.put(newId, newUser);
        return users.get(newId);
    }

    public User updateUser(User updatedUser) {
        User user = users.get(updatedUser.getId());
        user.setEmail(updatedUser.getEmail());
        user.setLogin(updatedUser.getLogin());
        user.setName(updatedUser.getName());
        user.setBirthday(updatedUser.getBirthday());
        return user;
    }

    public void deleteAllUsers() {
        users.clear();
    }

    public void deleteUserById(Integer id) {
        users.remove(id);
    }

    public Boolean isExistUser(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
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

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
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

    @Override
    public Collection<User> getFriends(Integer userId) {
        User user = getUserById(userId);
        Collection<Integer> friendsIds = user.getFriends();

        if (friendsIds == null) {
            return Collections.emptyList();
        }
        return getUsersByIds(friendsIds).values();
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();

        Set<Integer> userIds = userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toSet());

        return getUsersByIds(userIds).values();
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
