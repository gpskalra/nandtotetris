package com.nandtotetris.jackcompiler;

import java.util.HashMap;

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

    private static final HashMap<Character,ArithmeticCommand> unaryMap = new HashMap<>();
    private static final HashMap<Character,ArithmeticCommand> binaryMap = new HashMap<>();

    static {
        unaryMap.put('-',NEG);
        unaryMap.put('~',NOT);
    }

    static {
        binaryMap.put('+',ADD);
        binaryMap.put('-',SUB);
        binaryMap.put('=',EQ);
        binaryMap.put('>',GT);
        binaryMap.put('<',LT);
        binaryMap.put('&',AND);
        binaryMap.put('|',OR);
    }

    ArithmeticCommand(String name) {
        commandString = name;
    }

    public String getString() {
        return commandString;
    }

    public static ArithmeticCommand getCommandFromBinaryOp(char op) {
        return binaryMap.get(op);
    }

    public static ArithmeticCommand getCommandFromUnaryOp(char op) {
        return unaryMap.get(op);
    }
}
