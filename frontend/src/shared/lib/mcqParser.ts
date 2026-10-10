/**
 * Парсер MCQ-заданий.
 *
 * Бэк отдаёт задание одной строкой `body`, внутри которой уже подставлены
 * варианты ответа (формат `A) value`, `B) value` и т.д., разделитель — \n).
 *
 * Здесь мы разрезаем body на:
 *   - question: текст задания без вариантов
 *   - options: массив { id: 'A', value: '42' }
 *
 * Если по какой-то причине вариантов не найдено — возвращаем options = [].
 */

export interface ParsedMcq {
  question: string
  options: { id: string; value: string }[]
}

// Строка вида: "A) 42", "B) 43" и т.п. Регистр буквы — заглавный.
// Иногда вариант может содержать пробелы и знаки — берём всё после `)`.
const OPTION_LINE = /^([A-F])\)\s*(.*)$/

export function parseMcq(body: string): ParsedMcq {
  if (!body) return { question: '', options: [] }

  const lines = body.split('\n')
  const questionLines: string[] = []
  const options: { id: string; value: string }[] = []

  for (const line of lines) {
    const trimmed = line.trim()
    const match = OPTION_LINE.exec(trimmed)
    if (match) {
      options.push({ id: match[1], value: match[2] })
    } else if (options.length === 0) {
      questionLines.push(line)
    } else {
      // Варианты уже начались, а строки без match — считаем их
      // продолжением последнего варианта (редкий случай, если value многострочный).
      const last = options[options.length - 1]
      last.value += '\n' + line
    }
  }

  return {
    question: questionLines.join('\n').trim(),
    options,
  }
}