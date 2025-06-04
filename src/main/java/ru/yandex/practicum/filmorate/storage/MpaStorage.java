package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Map;

public interface MpaStorage {
    Map<Integer, Mpa> getMpas();

    Mpa getMpaById(Integer id);

    Boolean isExistMpa(Integer id);
}
