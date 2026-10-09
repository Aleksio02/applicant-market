CREATE TABLE IF NOT EXISTS specializations
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(64)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(512)
    );

CREATE TABLE IF NOT EXISTS grades
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(64)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    level       INT          NOT NULL,
    description VARCHAR(512)
    );

INSERT INTO specializations (code, name, description)
VALUES
    ('BACKEND',  'Backend-разработка',     'Серверная разработка, API, бизнес-логика'),
    ('FRONTEND', 'Frontend-разработка',    'Клиентская разработка, веб-интерфейсы'),
    ('DEVOPS',   'DevOps',                 'Инфраструктура, CI/CD, эксплуатация'),
    ('DATA',     'Data',                   'Анализ данных, ML, инженерия данных'),
    ('MOBILE',   'Мобильная разработка',   'iOS, Android, кроссплатформенные приложения'),
    ('QA',       'Тестирование',           'Ручное и автоматизированное тестирование')
    ON CONFLICT (code) DO NOTHING;

INSERT INTO grades (code, name, level, description)
VALUES
    ('JUNIOR', 'Junior', 1, 'Начинающий специалист, до 1 года опыта'),
    ('MIDDLE', 'Middle', 2, 'Специалист с опытом от 1 до 3 лет'),
    ('SENIOR', 'Senior', 3, 'Опытный специалист, от 3 лет'),
    ('LEAD',   'Lead',   4, 'Ведущий специалист, руководство командой')
    ON CONFLICT (code) DO NOTHING;