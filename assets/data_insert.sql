INSERT INTO exercise_types (_id, name) VALUES
(1, 'Базовое'),
(2, 'Формирующее'),
(3, 'Изолирующее');

INSERT INTO exercises (name, exercise_type) VALUES
('Жим штанги лежа', 1),
('Становая тяга', 1),
('Приседания', 1),
('Тяга штанги в наклоне', 1),
('Тяга к подбородку', 1),
('Жим штанги стоя', 1),
('Французский жим', 2);

INSERT INTO muscles (name) VALUES
('грудь'),
('спина'),
('плечи'),
('трицепс'),
('бицепс'),
('голень'),
('бедро');

INSERT INTO purposes (_id, name) VALUES
(1, 'Масса'),
(2, 'Сила'),
(3, 'Выносливость');

INSERT INTO programs (name, weeks, purpose, mesocycle) VALUES
('Линейный цикл', 4, 1, 1);
  INSERT INTO mesocycles (_id) VALUES
  (1);
    INSERT INTO cycles (_id, mesocycle) VALUES
    (1, 1),
    (2, 1),
    (3, 1),
    (4, 1);
      INSERT INTO trainings (_id, cycle) VALUES
      (1, 1),
      (2, 2),
      (3, 3),
      (4, 4);
        INSERT INTO sets (reps, weight, training) VALUES
        (10, 0.6, 1),
        (10, 0.6, 1),
        (10, 0.6, 1),
        (10, 0.7, 2),
        (10, 0.7, 2),
        (10, 0.7, 2),
        (8, 0.8, 3),
        (8, 0.8, 3),
        (8, 0.8, 3),
        (6, 0.9, 4),
        (6, 0.9, 4),
        (6, 0.9, 4);