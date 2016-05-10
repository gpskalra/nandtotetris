package com.nandtotetris.jackcompiler;

/**
 * Stores the following information for a symbol:
 * Type,
 * Kind: Static, field, argument, var
 * Index: Number assigned in the current scope
 */
public class SymbolTableEntry {

    private String mType;
    private SymbolKind mKind;
    private int mIndex;

    public SymbolTableEntry(String type,SymbolKind kind,int index) {
        mType = type;
        mKind = kind;
        mIndex = index;
    }

    public SymbolKind getKind() {
        return mKind;
    }

    public String getType() {
        return mType;
    }

    public int getIndex() {
        return mIndex;
    }
}