package com.nandtotetris.jackcompiler;

import java.util.HashMap;

/**
 * The kind of a jack symbol
 */
public enum SymbolKind {
    STATIC,
    FIELD,
    ARG,
    VAR,
    NONE;

    private static final HashMap<SymbolKind,Segment> segmentHashMap = new HashMap<>();

    static {
        segmentHashMap.put(STATIC,Segment.STATIC);
        segmentHashMap.put(FIELD,Segment.THIS);
        segmentHashMap.put(ARG,Segment.ARG);
        segmentHashMap.put(VAR,Segment.LOCAL);
    }

    public static Segment getSegment(SymbolKind kind) {
        return segmentHashMap.get(kind);
    }
}
