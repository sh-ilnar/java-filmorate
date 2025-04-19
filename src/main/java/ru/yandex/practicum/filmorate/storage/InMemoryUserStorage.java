package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
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


    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
