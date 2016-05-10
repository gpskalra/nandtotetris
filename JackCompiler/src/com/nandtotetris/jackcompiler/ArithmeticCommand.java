package com.nandtotetris.jackcompiler;

/**
 * VM Arithmetic commands
 */
public enum ArithmeticCommand {

    ADD("add"),
    SUB("sub"),
    NEG("neg"),
    EQ("eq"),
    GT("gt"),
    LT("lt"),
    AND("and"),
    OR("or"),
    NOT("not");

    private final String commandString;

    ArithmeticCommand(String name) {
        commandString = name;
    }

    public String getString() {
        return commandString;
    }
}
