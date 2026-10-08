-- Requires skills.code = 'JAVA'.

-- Difficulty 1
INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_PLUS_MCQ_D1', s.id, 'JAVA_BASICS', 'MCQ', 1,
    $tpl$Что выведет этот код?

int a = {a};
int b = {b};
System.out.println(a + b);

{options}$tpl$,
    $ps${"a":{"type":"int","min":1,"max":20},"b":{"type":"int","min":1,"max":20}}$ps$::jsonb,
    $as${"type":"mcq","correct_expr":"a + b","distractor_exprs":["a * b","concat(toString(a), toString(b))","a + b + 1"],"options_format":"{id}) {value}"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_MULT_MCQ_D1', s.id, 'JAVA_BASICS', 'MCQ', 1,
    $tpl$Что выведет этот код?

int a = {a};
int b = {b};
System.out.println(a * b);

{options}$tpl$,
    $ps${"a":{"type":"int","min":2,"max":9},"b":{"type":"int","min":2,"max":9}}$ps$::jsonb,
    $as${"type":"mcq","correct_expr":"a * b","distractor_exprs":["a + b","concat(toString(a), toString(b))","a * b + a"],"options_format":"{id}) {value}"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_MINUS_MCQ_D1', s.id, 'JAVA_BASICS', 'MCQ', 1,
    $tpl$Что выведет этот код?

int a = {a};
int b = {b};
System.out.println(a - b);

{options}$tpl$,
    $ps${"a":{"type":"int","min":10,"max":30},"b":{"type":"int","min":1,"max":9}}$ps$::jsonb,
    $as${"type":"mcq","correct_expr":"a - b","distractor_exprs":["b - a","a + b","a / b"],"options_format":"{id}) {value}"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_DOUBLE_MCQ_D1', s.id, 'JAVA_BASICS', 'MCQ', 1,
    $tpl$Что выведет этот код?

int a = {a};
System.out.println(a + a);

{options}$tpl$,
    $ps${"a":{"type":"int","min":1,"max":15}}$ps$::jsonb,
    $as${"type":"mcq","correct_expr":"a + a","distractor_exprs":["a * a","a","a + a + 1"],"options_format":"{id}) {value}"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_HALF_MCQ_D1', s.id, 'JAVA_BASICS', 'MCQ', 1,
    $tpl$Что выведет этот код?

int a = {a};
int b = 2;
System.out.println(a / b);

{options}$tpl$,
    $ps${"a":{"type":"int","min":2,"max":20}}$ps$::jsonb,
    $as${"type":"mcq","correct_expr":"a / 2","distractor_exprs":["a * 2","a","a - 2"],"options_format":"{id}) {value}"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_INC_MCQ_D1', s.id, 'JAVA_BASICS', 'MCQ', 1,
    $tpl$Что выведет этот код?

int a = {a};
int b = a++;
System.out.println(a);
System.out.println(b);

{options}$tpl$,
    $ps${"a":{"type":"int","min":1,"max":9}}$ps$::jsonb,
    $as${"type":"mcq","correct_expr":"concat(toString(a + 1), toString(a))","distractor_exprs":["concat(toString(a), toString(a))","concat(toString(a + 1), toString(a + 1))","concat(toString(a), toString(a + 1))"],"options_format":"{id}) {value}"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_STR_CONCAT_MCQ_D1', s.id, 'JAVA_STRINGS', 'MCQ', 1,
    $tpl$Что выведет этот код?

String a = "{sa}";
String b = "{sb}";
System.out.println(a + b);

{options}$tpl$,
    $ps${"sa":{"type":"string","alphabet":"abc","length":3},"sb":{"type":"string","alphabet":"xyz","length":2}}$ps$::jsonb,
    $as${"type":"mcq","correct_expr":"concat(sa, sb)","distractor_exprs":["concat(sb, sa)","concat(sa, sa)","concat(sb, sb)"],"options_format":"{id}) {value}"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_LOOP_COUNT_PREDICT_D1', s.id, 'JAVA_LOOPS', 'OUTPUT_PREDICT', 1,
    $tpl$Сколько раз выполнится тело цикла?

int count = 0;
for (int i = 0; i < {n}; i++) {
    count++;
}
System.out.println(count);

Введите результат:$tpl$,
    $ps${"n":{"type":"int","min":2,"max":15}}$ps$::jsonb,
    $as${"type":"value","expr":"n"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

-- Difficulty 2
INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_LOOP_SUM_PREDICT_D2', s.id, 'JAVA_LOOPS', 'OUTPUT_PREDICT', 2,
    $tpl$Что выведет этот код?

int sum = 0;
for (int i = 1; i <= {n}; i++) {
    sum += i;
}
System.out.println(sum);

Введите результат:$tpl$,
    $ps${"n":{"type":"int","min":3,"max":12}}$ps$::jsonb,
    $as${"type":"value","expr":"n * (n + 1) / 2"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_INTDIV_PREDICT_D2', s.id, 'JAVA_TYPES', 'OUTPUT_PREDICT', 2,
    $tpl$Что выведет этот код?

int a = {a};
int b = {b};
System.out.println(a / b);

Введите результат:$tpl$,
    $ps${"a":{"type":"int","min":20,"max":99},"b":{"type":"int","min":3,"max":9}}$ps$::jsonb,
    $as${"type":"value","expr":"a / b"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_ARRAY_BUG_D2', s.id, 'JAVA_ARRAYS', 'BUG_FIND', 2,
    $tpl$1. public int sum(int[] arr) {
2.     int result = 0;
3.     for (int i = 0; i <= arr.length; i++) {
4.         result += arr[i];
5.     }
6.     return result;
7. }

В какой строке ошибка? (введите номер строки)$tpl$,
    $ps${}$ps$::jsonb,
    $as${"type":"value","expr":"3"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

-- Difficulty 3
INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_MOD_NEG_MCQ_D3', s.id, 'JAVA_TYPES', 'MCQ', 3,
    $tpl$Что выведет этот код?

int a = -{x};
int b = {y};
System.out.println(a % b);

{options}$tpl$,
    $ps${"x":{"type":"int","min":5,"max":20},"y":{"type":"int","min":2,"max":5}}$ps$::jsonb,
    $as${"type":"mcq","correct_expr":"(0 - x) % y","distractor_exprs":["x % y","0 - (x + y)","(0 - x) / y"],"options_format":"{id}) {value}"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_MIXED_PREDICT_D3', s.id, 'JAVA_OPERATORS', 'OUTPUT_PREDICT', 3,
    $tpl$Что выведет этот код?

int a = {a};
int b = {b};
int c = {c};
int r = a * b - c;
System.out.println(r);

Введите результат:$tpl$,
    $ps${"a":{"type":"int","min":3,"max":9},"b":{"type":"int","min":2,"max":8},"c":{"type":"int","min":1,"max":25}}$ps$::jsonb,
    $as${"type":"value","expr":"a * b - c"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_NPE_BUG_D3', s.id, 'JAVA_EXCEPTIONS', 'BUG_FIND', 3,
    $tpl$1. public String greet(String name) {
2.     return "Hello, " + name.toUpperCase();
3. }

При вызове greet(null) в какой строке произойдёт ошибка?
Введите номер строки:$tpl$,
    $ps${}$ps$::jsonb,
    $as${"type":"value","expr":"2"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

-- Difficulty 4
INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_SQUARE_DIFF_MCQ_D4', s.id, 'JAVA_OPERATORS', 'MCQ', 4,
    $tpl$Что выведет этот код?

int a = {a};
int b = {b};
int r = a * a + b * b - 2 * a * b;
System.out.println(r);

{options}$tpl$,
    $ps${"a":{"type":"int","min":5,"max":15},"b":{"type":"int","min":1,"max":14}}$ps$::jsonb,
    $as${"type":"mcq","correct_expr":"a * a + b * b - 2 * a * b","distractor_exprs":["a * a - b * b","a * a + b * b","(a + b) * (a + b)"],"options_format":"{id}) {value}"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_NESTED_PREDICT_D4', s.id, 'JAVA_OPERATORS', 'OUTPUT_PREDICT', 4,
    $tpl$Что выведет этот код?

int a = {a};
int b = {b};
int c = {c};
int r = a * (b + c) - b * (a - c);
System.out.println(r);

Введите результат:$tpl$,
    $ps${"a":{"type":"int","min":3,"max":9},"b":{"type":"int","min":2,"max":8},"c":{"type":"int","min":1,"max":6}}$ps$::jsonb,
    $as${"type":"value","expr":"a * (b + c) - b * (a - c)"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_RACE_BUG_D4', s.id, 'JAVA_CONCURRENCY', 'BUG_FIND', 4,
    $tpl$1. public class Counter {
2.     private int count = 0;
3.     public void increment() {
4.         count++;
5.     }
6. }

При вызове increment() из нескольких потоков в какой строке возникнет проблема?
Введите номер строки:$tpl$,
    $ps${}$ps$::jsonb,
    $as${"type":"value","expr":"4"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

-- Difficulty 5
INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_LARGE_CALC_PREDICT_D5', s.id, 'JAVA_OPERATORS', 'OUTPUT_PREDICT', 5,
    $tpl$Что выведет этот код?

int a = {a};
int b = {b};
int r = a * a * b - b * b * a + a * b;
System.out.println(r);

Введите результат:$tpl$,
    $ps${"a":{"type":"int","min":10,"max":30},"b":{"type":"int","min":5,"max":15}}$ps$::jsonb,
    $as${"type":"value","expr":"a * a * b - b * b * a + a * b"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;

INSERT INTO assessment_templates
(code, skill_id, topic, type, difficulty, body_template, parameter_spec, answer_spec, is_active)
SELECT
    'JAVA_DEADLOCK_BUG_D5', s.id, 'JAVA_CONCURRENCY', 'BUG_FIND', 5,
    $tpl$1. synchronized (lockA) {
2.     synchronized (lockB) {
3.         doWork();
4.     }
5. }
6.
7. synchronized (lockB) {
8.     synchronized (lockA) {
9.         doOtherWork();
10.    }
11. }

Поток 1 выполняет блок со строк 1–5, поток 2 — со строк 7–11.
Возможен ли deadlock?
Введите номер строки, где поток 1 захватывает второй lock:$tpl$,
    $ps${}$ps$::jsonb,
    $as${"type":"value","expr":"2"}$as$::jsonb,
    true
FROM skills s WHERE s.code = 'JAVA'
ON CONFLICT (code) DO NOTHING;