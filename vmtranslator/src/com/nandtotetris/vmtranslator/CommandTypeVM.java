package com.nandtotetris.vmtranslator;

/**
 * Types of commands in the VM language
 * @author gaganpreet1810@gmail.com
 */
public enum CommandTypeVM {

    // nine stack oriented commands
    C_ARITHMETIC,

    // memory access commands
    C_PUSH,
    C_POP,

    // program flow commands
    C_LABEL,
    C_GOTO,
    C_IF,

    // function calling commands
    C_FUNCTION,
    C_RETURN,
    C_CALL
}
