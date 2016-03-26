package com.nandtotetris.vmtranslator;

/**
 * The assembly command types.
 */
public enum CommandTypeAsm {

    // @(symbol|decimal)
    A_COMMAND,

    // dest=comp;jump
    C_COMMAND,

    // pseudo-command: (label)
    L_COMMAND
}
