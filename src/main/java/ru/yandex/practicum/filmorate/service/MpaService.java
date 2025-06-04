package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<Mpa> getAllMpas() {
        return mpaStorage.getMpas().values();
    }

    public Mpa getMpaById(Integer id) {
        if (!mpaStorage.isExistMpa(id)) {
            throw new NotFoundException("Рейтинг MPA с указанным идентификатором не найден");
        }
        return mpaStorage.getMpaById(id);
    }
}
