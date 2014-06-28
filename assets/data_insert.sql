INSERT INTO exercise_types (_id, name) VALUES
(1, 'Базовое');
INSERT INTO exercise_types (_id, name) VALUES
(2, 'Формирующее');
INSERT INTO exercise_types (_id, name) VALUES
(3, 'Изолирующее');

INSERT INTO exercises (name, exercise_type) VALUES
('Жим штанги лежа', 1);
INSERT INTO exercises (name, exercise_type) VALUES
('Становая тяга', 1);
INSERT INTO exercises (name, exercise_type) VALUES
('Приседания', 1);
INSERT INTO exercises (name, exercise_type) VALUES
('Тяга штанги в наклоне', 1);
INSERT INTO exercises (name, exercise_type) VALUES
('Тяга к подбородку', 1);
INSERT INTO exercises (name, exercise_type) VALUES
('Жим штанги стоя', 1);
INSERT INTO exercises (name, exercise_type) VALUES
('Французский жим', 2);
INSERT INTO exercises (name, exercise_type) VALUES
('Жим штанги лежа 45', 2);
INSERT INTO exercises (name, exercise_type) VALUES
('Разводка гантелей лежа', 3);
INSERT INTO exercises (name, exercise_type) VALUES
('Подъем штанги на бицепс', 1);
INSERT INTO exercises (name, exercise_type) VALUES
('Молоток', 2);

INSERT INTO purposes (_id, name) VALUES
(1, 'Масса');
INSERT INTO purposes (_id, name) VALUES
(2, 'Сила');
INSERT INTO purposes (_id, name) VALUES
(3, 'Выносливость');

INSERT INTO programs (name, weeks, purpose, mesocycle) VALUES
('Линейный цикл', 4, 1, 1);
  INSERT INTO mesocycles (_id, trainings_in_week) VALUES
  (1, 1);
      INSERT INTO trainings (_id, mesocycle) VALUES
      (1, 1);
      INSERT INTO trainings (_id, mesocycle) VALUES
      (2, 1);
      INSERT INTO trainings (_id, mesocycle) VALUES
      (3, 1);
      INSERT INTO trainings (_id, mesocycle) VALUES
      (4, 1);
        INSERT INTO sets (reps, weight, training) VALUES
        (10, 0.6, 1);
        INSERT INTO sets (reps, weight, training) VALUES
        (10, 0.6, 1);
        INSERT INTO sets (reps, weight, training) VALUES
        (10, 0.6, 1);
        INSERT INTO sets (reps, weight, training) VALUES
        (10, 0.7, 2);
        INSERT INTO sets (reps, weight, training) VALUES
        (10, 0.7, 2);
        INSERT INTO sets (reps, weight, training) VALUES
        (10, 0.7, 2);
        INSERT INTO sets (reps, weight, training) VALUES
        (8, 0.8, 3);
        INSERT INTO sets (reps, weight, training) VALUES
        (8, 0.8, 3);
        INSERT INTO sets (reps, weight, training) VALUES
        (8, 0.8, 3);
        INSERT INTO sets (reps, weight, training) VALUES
        (6, 0.9, 4);
        INSERT INTO sets (reps, weight, training) VALUES
        (6, 0.9, 4);
        INSERT INTO sets (reps, weight, training) VALUES
        (6, 0.9, 4);