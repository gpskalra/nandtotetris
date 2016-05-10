package com.nandtotetris.jackcompiler;

/**
 * VM Segment used by the VM Writer
 * to output VM code
 */
public enum Segment {

    CONST("constant"),
    ARG("argument"),
    LOCAL("local"),
    STATIC("static"),
    THIS("this"),
    THAT("that"),
    POINTER("pointer"),
    TEMP("temp");

    private final String segmentString;

    public String getString() {
        return segmentString;
    }

    Segment(String name) {
        segmentString = name;
    }
}
