INSERT INTO categories (specialization_id, grade_id, code, name)
SELECT
    s.id,
    g.id,
    s.code || '_' || g.code,
    s.name || ' ' || g.name
FROM specializations s CROSS JOIN grades g ON CONFLICT DO NOTHING;