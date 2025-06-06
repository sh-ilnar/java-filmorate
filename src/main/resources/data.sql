-- Вставка рейтингов MPA
INSERT INTO mpa (name, description) VALUES
('G', 'Нет возрастных ограничений'),
('PG', 'Рекомендуется присутствие родителей'),
('PG-13', 'Детям до 13 лет просмотр не желателен'),
('R', 'Лицам до 17 лет обязательно присутствие взрослого'),
('NC-17', 'Лицам до 18 лет просмотр запрещён');

-- Вставка жанров
INSERT INTO genres (name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');

-- Вставка пользователей
INSERT INTO users (login, email, name, birthday) VALUES
('user_1', 'user1@example.com', 'Иван Иванов', '1990-05-15'),
('user_2', 'user2@example.com', 'Петр Петров', '1985-08-22'),
('user_3', 'user3@example.com', 'Александр Александров', '1995-03-10'),
('user_4', 'user4@example.com', 'Анна Козлова', '2000-11-30'),
('user_5', 'user5@example.com', 'Константин Константинов', '2001-11-21'),
('user_6', 'user6@example.com', 'Роман Романов', '2002-12-12'),
('user_7', 'user7@example.com', 'Лада Ладова', '2003-10-21');

-- Вставка фильмов
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES
('Матрица', 'Фильм о виртуальной реальности', '1999-03-31', 136, 4),
('Король Лев', 'Мультфильм о львенке Симбе', '1994-06-24', 88, 1),
('Побег из Шоушенка', 'Драма о несправедливо осужденном банкире', '1994-09-23', 142, 4),
('Мальчишник в Вегасе', 'Комедия о незабываемом мальчишнике', '2009-06-01', 100, 3),
('Интерстеллар', 'Фантастика о путешествии через червоточину', '2014-11-06', 169, 3);

-- Вставка связей фильмов и жанров
INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 6), (1, 4),  -- Матрица: Боевик, Триллер
(2, 3), (2, 2),  -- Король Лев: Мультфильм, Драма
(3, 2),          -- Побег из Шоушенка: Драма
(4, 1),          -- Мальчишник в Вегасе: Комедия
(5, 2), (5, 4);  -- Интерстеллар: Драма, Триллер

-- Вставка лайков
INSERT INTO likes (film_id, user_id, created_at) VALUES
(1, 1, '2023-01-10 12:00:00'),
(1, 2, '2023-01-11 13:30:00'),
(2, 1, '2023-01-12 14:45:00'),
(2, 3, '2023-01-13 15:20:00'),
(3, 4, '2023-01-14 16:10:00'),
(4, 2, '2023-01-15 17:30:00'),
(5, 1, '2023-01-16 18:00:00'),
(5, 3, '2023-01-17 19:15:00');

-- Вставка запросов на дружбу
INSERT INTO friend_requests (user_id, friend_id, created_at) VALUES
(1, 2, '2023-01-10 10:00:00'),
(1, 3, '2023-01-11 11:00:00'),
(2, 3, '2023-01-12 12:00:00'),
(3, 4, '2023-01-13 13:00:00');