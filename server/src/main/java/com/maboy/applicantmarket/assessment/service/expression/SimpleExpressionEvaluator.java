package com.maboy.applicantmarket.assessment.service.expression;

import com.maboy.applicantmarket.assessment.model.exception.ExpressionEvaluationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SimpleExpressionEvaluator implements ExpressionEvaluator {

    @Override
    public Object evaluate(String expression, Map<String, Object> parameters) {
        if (expression == null || expression.isBlank()) {
            throw new ExpressionEvaluationException("Expression is null or blank");
        }
        List<Token> tokens = new Tokenizer(expression).tokenize();
        Parser parser = new Parser(expression, tokens, parameters);
        Object result = parser.parseExpression();
        parser.expectEof();
        return result;
    }

    // ============================================================
    // Parser — рекурсивный спуск по грамматике
    //   expr   := term (('+' | '-') term)*
    //   term   := factor (('*' | '/' | '%') factor)*
    //   factor := '-' factor | atom
    //   atom   := NUMBER | STRING | IDENT | func_call | '(' expr ')'
    //   func_call := IDENT '(' (expr (',' expr)*)? ')'
    // ============================================================
    private static final class Parser {

        private final String source;
        private final List<Token> tokens;
        private final Map<String, Object> parameters;
        private int idx = 0;

        Parser(String source, List<Token> tokens, Map<String, Object> parameters) {
            this.source = source;
            this.tokens = tokens;
            this.parameters = parameters;
        }

        Object parseExpression() {
            Object left = parseTerm();
            while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
                Token op = advance();
                Object right = parseTerm();
                left = op.type() == TokenType.PLUS ? plus(left, right) : minus(left, right);
            }
            return left;
        }

        private Object parseTerm() {
            Object left = parseFactor();
            while (check(TokenType.STAR) || check(TokenType.SLASH) || check(TokenType.PERCENT)) {
                Token op = advance();
                Object right = parseFactor();
                left = switch (op.type()) {
                    case STAR -> multiply(left, right);
                    case SLASH -> divide(left, right);
                    case PERCENT -> modulo(left, right);
                    default -> throw new ExpressionEvaluationException("Unreachable");
                };
            }
            return left;
        }

        private Object parseFactor() {
            if (check(TokenType.MINUS)) {
                advance();
                Object value = parseFactor();
                return negate(value);
            }
            return parseAtom();
        }

        private Object parseAtom() {
            Token token = peek();
            switch (token.type()) {
                case NUMBER, STRING -> {
                    advance();
                    return token.value();
                }
                case IDENT -> {
                    advance();
                    if (check(TokenType.LPAREN)) {
                        return parseFunctionCall((String) token.value(), token.position());
                    }
                    return resolveParameter((String) token.value(), token.position());
                }
                case LPAREN -> {
                    advance();
                    Object value = parseExpression();
                    expect(TokenType.RPAREN);
                    return value;
                }
                default -> throw new ExpressionEvaluationException(
                        "Unexpected token " + token + " in: " + source);
            }
        }

        private Object parseFunctionCall(String name, int position) {
            expect(TokenType.LPAREN);
            List<Object> args = new ArrayList<>();
            if (!check(TokenType.RPAREN)) {
                args.add(parseExpression());
                while (check(TokenType.COMMA)) {
                    advance();
                    args.add(parseExpression());
                }
            }
            expect(TokenType.RPAREN);
            return invokeFunction(name, args, position);
        }

        private Object resolveParameter(String name, int position) {
            if (!parameters.containsKey(name)) {
                throw new ExpressionEvaluationException(
                        "Unknown parameter '" + name + "' at position " + position +
                        " in: " + source);
            }
            return parameters.get(name);
        }

        // ---------------- Функции ----------------

        private Object invokeFunction(String name, List<Object> args, int position) {
            return switch (name) {
                case "concat" -> {
                    StringBuilder sb = new StringBuilder();
                    for (Object a : args) sb.append(toString(a));
                    yield sb.toString();
                }
                case "len" -> {
                    requireArgs(name, args, 1, position);
                    Object v = args.get(0);
                    if (v instanceof String s) yield (long) s.length();
                    if (v instanceof List<?> l) yield (long) l.size();
                    throw new ExpressionEvaluationException(
                            "len() expects string or list, got " + typeName(v));
                }
                case "get" -> {
                    requireArgs(name, args, 2, position);
                    Object list = args.get(0);
                    if (!(list instanceof List<?> l)) {
                        throw new ExpressionEvaluationException(
                                "get() expects list as first argument, got " + typeName(list));
                    }
                    long i = toLong(args.get(1));
                    if (i < 0 || i >= l.size()) {
                        throw new ExpressionEvaluationException(
                                "get() index out of bounds: " + i + " (size=" + l.size() + ")");
                    }
                    yield l.get((int) i);
                }
                case "toString" -> {
                    requireArgs(name, args, 1, position);
                    yield toString(args.get(0));
                }
                case "int" -> {
                    requireArgs(name, args, 1, position);
                    yield toLong(args.get(0));
                }
                default -> throw new ExpressionEvaluationException(
                        "Unknown function '" + name + "' at position " + position);
            };
        }

        private void requireArgs(String fn, List<Object> args, int expected, int position) {
            if (args.size() != expected) {
                throw new ExpressionEvaluationException(
                        "Function " + fn + "() expects " + expected +
                        " argument(s), got " + args.size() + " at position " + position);
            }
        }

        // ---------------- Операции ----------------

        private Object plus(Object left, Object right) {
            // Строки выигрывают: если хоть один — строка, конкатенируем.
            if (left instanceof String || right instanceof String) {
                return toString(left) + toString(right);
            }
            return toLong(left) + toLong(right);
        }

        private Object minus(Object left, Object right) {
            return toLong(left) - toLong(right);
        }

        private Object multiply(Object left, Object right) {
            return toLong(left) * toLong(right);
        }

        private Object divide(Object left, Object right) {
            long r = toLong(right);
            if (r == 0) {
                throw new ExpressionEvaluationException("Division by zero in: " + source);
            }
            return toLong(left) / r;
        }

        private Object modulo(Object left, Object right) {
            long r = toLong(right);
            if (r == 0) {
                throw new ExpressionEvaluationException("Modulo by zero in: " + source);
            }
            return toLong(left) % r;
        }

        private Object negate(Object value) {
            return -toLong(value);
        }

        // ---------------- Утилиты ----------------

        private long toLong(Object value) {
            if (value instanceof Long l) return l;
            if (value instanceof Integer i) return i.longValue();
            if (value instanceof Short s) return s.longValue();
            if (value instanceof String s) {
                try {
                    return Long.parseLong(s.trim());
                } catch (NumberFormatException e) {
                    throw new ExpressionEvaluationException(
                            "Cannot convert string '" + s + "' to number");
                }
            }
            throw new ExpressionEvaluationException(
                    "Cannot convert " + typeName(value) + " to number");
        }

        private String toString(Object value) {
            if (value == null) return "";
            return String.valueOf(value);
        }

        private String typeName(Object value) {
            return value == null ? "null" : value.getClass().getSimpleName();
        }

        // ---------------- Поток токенов ----------------

        private Token peek() {
            return tokens.get(idx);
        }

        private Token advance() {
            return tokens.get(idx++);
        }

        private boolean check(TokenType type) {
            return peek().type() == type;
        }

        private void expect(TokenType type) {
            if (!check(type)) {
                throw new ExpressionEvaluationException(
                        "Expected " + type + " but got " + peek() + " in: " + source);
            }
            advance();
        }

        void expectEof() {
            if (!check(TokenType.EOF)) {
                throw new ExpressionEvaluationException(
                        "Unexpected trailing token " + peek() + " in: " + source);
            }
        }
    }
}