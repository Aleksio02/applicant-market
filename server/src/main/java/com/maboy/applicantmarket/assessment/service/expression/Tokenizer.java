package com.maboy.applicantmarket.assessment.service.expression;

import com.maboy.applicantmarket.assessment.model.exception.ExpressionEvaluationException;

import java.util.ArrayList;
import java.util.List;

final class Tokenizer {

    private final String input;
    private int pos = 0;

    Tokenizer(String input) {
        this.input = input;
    }

    List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }
            if (c == '+') { tokens.add(Token.of(TokenType.PLUS, pos++)); continue; }
            if (c == '-') { tokens.add(Token.of(TokenType.MINUS, pos++)); continue; }
            if (c == '*') { tokens.add(Token.of(TokenType.STAR, pos++)); continue; }
            if (c == '/') { tokens.add(Token.of(TokenType.SLASH, pos++)); continue; }
            if (c == '%') { tokens.add(Token.of(TokenType.PERCENT, pos++)); continue; }
            if (c == '(') { tokens.add(Token.of(TokenType.LPAREN, pos++)); continue; }
            if (c == ')') { tokens.add(Token.of(TokenType.RPAREN, pos++)); continue; }
            if (c == ',') { tokens.add(Token.of(TokenType.COMMA, pos++)); continue; }
            if (Character.isDigit(c)) { tokens.add(readNumber()); continue; }
            if (c == '"') { tokens.add(readString()); continue; }
            if (Character.isLetter(c) || c == '_') { tokens.add(readIdent()); continue; }

            throw new ExpressionEvaluationException(
                    "Unexpected character '" + c + "' at position " + pos + " in: " + input);
        }
        tokens.add(Token.of(TokenType.EOF, pos));
        return tokens;
    }

    private Token readNumber() {
        int start = pos;
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            pos++;
        }
        String raw = input.substring(start, pos);
        try {
            return Token.of(TokenType.NUMBER, Long.parseLong(raw), start);
        } catch (NumberFormatException e) {
            throw new ExpressionEvaluationException(
                    "Number too large: " + raw + " at position " + start, e);
        }
    }

    private Token readString() {
        int start = pos;
        pos++; // skip opening quote
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && input.charAt(pos) != '"') {
            char c = input.charAt(pos);
            if (c == '\\' && pos + 1 < input.length()) {
                pos++;
                char esc = input.charAt(pos);
                switch (esc) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(esc);
                }
                pos++;
                continue;
            }
            sb.append(c);
            pos++;
        }
        if (pos >= input.length()) {
            throw new ExpressionEvaluationException(
                    "Unterminated string starting at position " + start);
        }
        pos++; // skip closing quote
        return Token.of(TokenType.STRING, sb.toString(), start);
    }

    private Token readIdent() {
        int start = pos;
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isLetterOrDigit(c) || c == '_') {
                pos++;
            } else {
                break;
            }
        }
        return Token.of(TokenType.IDENT, input.substring(start, pos), start);
    }
}