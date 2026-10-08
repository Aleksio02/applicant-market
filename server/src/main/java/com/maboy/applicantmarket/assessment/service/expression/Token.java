package com.maboy.applicantmarket.assessment.service.expression;

public record Token(TokenType type, Object value, int position) {

    public static Token of(TokenType type, int position) {
        return new Token(type, null, position);
    }

    public static Token of(TokenType type, Object value, int position) {
        return new Token(type, value, position);
    }

    @Override
    public String toString() {
        return value == null ? type.name() : type + "(" + value + ")";
    }
}