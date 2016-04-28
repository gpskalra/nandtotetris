package com.nandtotetris.jackcompiler;

import java.util.HashMap;

/**
 * The keywords specified by the Jack grammar.
 * Provides method to look up a keywordString from the
 * keywordString string.
 */
public enum Keyword {


    CLASS("class"),
    CONSTRUCTOR("constructor"),
    FUNCTION("function"),
    METHOD("method"),
    FIELD("field"),
    STATIC("static"),
    VAR("var"),
    INT("int"),
    CHAR("char"),
    BOOLEAN("boolean"),
    VOID("void"),
    TRUE("true"),
    FALSE("false"),
    NULL("null"),
    THIS("this"),
    LET("let"),
    DO("do"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    RETURN("return");

    private final String keywordString;

    private static final HashMap<String,Keyword> lookUp = new HashMap<>();

    static {
        for (Keyword k : Keyword.values()) {
            lookUp.put(k.keywordString,k);
        }
    }

    Keyword(String keyword) {
        this.keywordString = keyword;
    }

    // returns the enum corresponding to the keywordString string.
    // returns null if the input string is not a keywordString
    public static Keyword get(String k) {
        return lookUp.get(k);
    }

    public String getKeywordString() {
        return keywordString;
    }
}
