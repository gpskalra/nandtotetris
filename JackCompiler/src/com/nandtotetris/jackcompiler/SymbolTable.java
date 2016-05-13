package com.nandtotetris.jackcompiler;

import java.util.*;

/**
 * This class provides symbol table services for the
 * jack compiler.
 * Each symbol in the jack code has an associated scope:
 * class scope or subroutine scope.
 *
 * The symbol table stores information for the following
 * kinds of identifiers.
 * Kind ----- Scope
 * Static     Class
 * Field      Class
 * Argument   Subroutine
 * Var        Subroutine
 *
 * The symbol table stores the following information for each
 * symbol:
 * Name
 * Type
 * Kind: Static, field, argument, var
 * Index: Number assigned in the current scope
 */
public class SymbolTable {

    private Map<String,SymbolTableEntry> classScopeTable;
    private Map<String,SymbolTableEntry> subroutineScopeTable;
    private Map<SymbolKind,Integer> numSymbols;

    /**
     * Creates a new empty symbol table
     */
    public SymbolTable() {
        classScopeTable = new Hashtable<>();
        subroutineScopeTable = new Hashtable<>();
        numSymbols = new HashMap<>();
        numSymbols.put(SymbolKind.STATIC,0);
        numSymbols.put(SymbolKind.FIELD,0);
        numSymbols.put(SymbolKind.ARG,0);
        numSymbols.put(SymbolKind.VAR,0);
    }

    /**
     * Starts a new subroutine scope
     */
    public void startSubroutine() {
        subroutineScopeTable.clear();
        numSymbols.put(SymbolKind.ARG,0);
        numSymbols.put(SymbolKind.VAR,0);
    }

    /**
     * Returns true if an input symbol kind has
     * class scope
     * @param kind The input SymbolKind
     * @return true if SymbolKind has class scope
     *         false otherwise.
     */
    private boolean hasClassScope(SymbolKind kind) {
        switch (kind) {
            case STATIC:
            case FIELD:
                return true;
            case ARG:
            case VAR:
                return false;
            default:
                return false;
        }
    }

    /**
     * Defines a new identifier of a given name,
     * type and kind. Also assigns it a running
     * index. STATIC and FIELD identifiers have
     * class scope while ARG and VAR have
     * subroutine scope.
     *
     * @param name The name of the identifier
     * @param type The type of the identifier
     * @param kind The SymbolKind of the identifier.
     */
    public void define(String name,String type,SymbolKind kind) {

        int index = numSymbols.get(kind);
        numSymbols.put(kind,index+1);

        SymbolTableEntry entry = new SymbolTableEntry(type,kind,index);

        if (hasClassScope(kind)) {
            classScopeTable.put(name,entry);
            System.out.println("Symbol Added to classScopeTable");
        }

        else {
            subroutineScopeTable.put(name,entry);
            System.out.println("Symbol Added to subroutineScopeTable");
        }
        System.out.println("Name: " + name);
        System.out.println("Type: " + typeOf(name));
        System.out.println("Kind: " + kindOf(name));
        System.out.println("Index: " + indexOf(name));
    }

    /**
     * Returns the symbol kind given the name
     * of the symbol. Returns NONE if the symbol
     * is not found in the current scope.
     *
     * @param name The name of the input symbol
     * @return The SymbolKind of the input symbol.
     *         NONE if symbol is not found in the
     *         current scope.
     */
    public SymbolKind kindOf(String name) {

        if (subroutineScopeTable.containsKey(name)) {
            return subroutineScopeTable.get(name).getKind();
        }

        if (classScopeTable.containsKey(name)) {
            return classScopeTable.get(name).getKind();
        }

        return SymbolKind.NONE;
    }

    /**
     * Returns the type of the named identifier
     * in the current scope.
     * Returns null if the symbol is not found in
     * the current scope.
     * @param name the name of the identifier
     * @return the type of the identifier.
     */
    public String typeOf(String name) {
        if (subroutineScopeTable.containsKey(name)) {
            return subroutineScopeTable.get(name).getType();
        }
        if (classScopeTable.containsKey(name)) {
            return classScopeTable.get(name).getType();
        }
        return null;
    }

    /**
     * Returns the index assigned to the
     * named identifier.
     * Returns -1 if the symbol is not found
     * in the current scope.
     * @param name the name of the identifier.
     * @return the running index assigned to the
     *         identifier.
     */
    public int indexOf(String name) {
        if (subroutineScopeTable.containsKey(name)) {
            return subroutineScopeTable.get(name).getIndex();
        }
        if (classScopeTable.containsKey(name)) {
            return classScopeTable.get(name).getIndex();
        }
        return -1;
    }

    /**
     * Returns the number of variables of the given
     * kind already defined in the current scope.
     * @param kind the input SymbolKind
     * @return The number of variables of SymbolKind
     *         kind already defined.
     */
    public int varCount(SymbolKind kind) {
        return numSymbols.get(kind);
    }
}
