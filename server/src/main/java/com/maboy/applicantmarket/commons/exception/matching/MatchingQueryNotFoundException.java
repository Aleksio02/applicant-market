package com.maboy.applicantmarket.commons.exception.matching;

public class MatchingQueryNotFoundException extends MatchingException {
    public MatchingQueryNotFoundException(String message) { super(message); }
    public static MatchingQueryNotFoundException byId(Object id) {
        return new MatchingQueryNotFoundException("Matching query not found: " + id);
    }
}