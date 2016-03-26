package com.nandtotetris.assembler;

public enum CommandType {

    // @(symbol|decimal)
    A_COMMAND,

    // dest=comp;jump
    C_COMMAND,

    // pseudo-command: (label)
    L_COMMAND
}

