package com.nandtotetris.assembler;

import java.util.HashMap;
import java.util.Map;

/**
 * This class stores <symbol,value> pairs.
 *
 * @author gaganpreet1810@gmail.com
 */
public class SymbolTable {

    private Map<String,Integer> table;

    /**
     * Initializes a new empty symbol table
     */
    public SymbolTable() {
        table = new HashMap<String,Integer>();
    }

    /**
     * Adds a <symbol,address> entry to the symbol table
     * Should only be called when contains returns false
     *
     * @param symbol The symbol to add
     * @param address The address corresponding to the symbol
     */
    public void addEntry(String symbol,Integer address) {
        table.put(symbol, address);
        return;
    }

    /**
     * Adds pre defined symbols to the symbol table
     */
    public void addPreDefinedSymbols() {
        table.put("SP",0);   table.put("LCL",1); table.put("ARG",2); table.put("THIS",3);
        table.put("THAT",4);
        table.put("R0",0);   table.put("R1",1);  table.put("R2",2);
        table.put("R3",3);   table.put("R4",4);  table.put("R5",5);  table.put("R6",6);
        table.put("R7",7);   table.put("R8",8);  table.put("R9",9);  table.put("R10",10);
        table.put("R11",11); table.put("R12",12);table.put("R13",13);table.put("R14",14);
        table.put("R15",15);
        table.put("SCREEN",16384);
        table.put("KBD", 24576);
    }

    /**
     * returns true if the symbol table contains <symbol,value> pair
     * for the given symbol
     *
     * @param symbol The string to check if present in the symbol table
     * @return true if symbol table contains <symbol,value> pair
     */
    public Boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    /**
     * returns the address associated with an input symbol
     * Should only be called when contains returns true.
     *
     * @param symbol
     * @return the integer address associated with symbol
     */
    public Integer getAddress(String symbol) {
        return table.get(symbol);
    }
}