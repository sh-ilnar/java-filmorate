package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public Collection<Mpa> getAllMpas() {
        log.info("Получен запрос на получение всех рейтингов MPA");
        return mpaService.getAllMpas();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(
            @PathVariable("id") Integer id
    ) {
        log.info("Получен запрос на получение рейтинга MPA с идентификатором: {}", id);
        return mpaService.getMpaById(id);
    }
}
