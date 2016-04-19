package com.nandtotetris.jackcompiler;

import java.util.HashMap;

/**
 * The keywords specified by the Jack grammar.
 * Provides method to look up a keyword from the
 * keyword string.
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

    private String keyword;

    private static final HashMap<String,Keyword> lookUp = new HashMap<>();

    static {
        for (Keyword k : Keyword.values()) {
            lookUp.put(k.keyword,k);
        }
    }

    Keyword(String keyword) {
        this.keyword = keyword;
    }

    // returns the enum corresponding to the keyword string.
    // returns null if the input string is not a keyword
    public static Keyword get(String k) {
        return lookUp.get(k);
    }

}
