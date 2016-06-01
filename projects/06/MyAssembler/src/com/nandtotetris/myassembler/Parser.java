package com.nandtotetris.myassembler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class encapsulates access to input code. It 
 * provides access to various components of a 
 * command.
 * 
 * @author gaganpreet1810@gmail.com
 */
public class Parser {
	
	private RandomAccessFile inputFile = null;
		
	private String currentCommand;
	
    Parser(String filename) throws FileNotFoundException {
 
    	inputFile = new RandomAccessFile(filename,"r");
    	currentCommand = null;
    }
    
    /**
     * Checks if an input line contains a command
     * 
     * @param line input line of assembly language input file
     * @return     true if line contains a command
     */
    private Boolean hasACommand(String line) {
    	
    	// An input line that matches this regular expression
    	// does not contain a command
    	String whitespaceOrComment = "(\\s*)|\\s*//.*";
    	
    	/*if(line.matches(whitespaceOrComment)) {
    		System.out.println("hasACommand: " + line + " does not contain a command");	
    	}*/
    	
    	return !line.matches(whitespaceOrComment);
    }
    
    /**
     * Returns true if the input file has more commands
     * after the current file pointer location
     * 
     * @return true if the input file has more commands
     *         after the current file pointer
     */
    public Boolean hasMoreCommands() {
    	
    	String line = null;
    	long currentPosition;
    	
    	do {
    	    try {
    	    	
    	    	currentPosition = inputFile.getFilePointer();
				line = inputFile.readLine();
				
				if(line == null)
					return false;
				
				if(hasACommand(line)) {
					
					// set the file pointer so that advance method can access the command
					inputFile.seek(currentPosition);
					
					return true;					
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    
    	} while (true);
    }
    
    /**
     * Reads the next command from the input and makes it the current
     * command. Should be called only if hasMoreCommands() is true.
     * Initially there is no current command.
     */
    public void advance() {
        try {
			String line = inputFile.readLine();
			
			// remove comments 
			String commentPattern = "//.*";
			line = line.replaceAll(commentPattern,"");
			
			// remove white spaces
			String whitespaces = "\\s";
			line = line.replaceAll(whitespaces,"");
			
			currentCommand = line;
			
			// System.out.println("advance: Current command = " + currentCommand);
		
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    /**
     * returns the commandType of the currentCommand
     * Ensure that advance method has been called to set 
     * currentCommand
     * 
     * @return CommandType A_COMMAND for @symbol|decimal
     *                     L_COMMAND for (LABEL)
     *                     C_COMMAND for dest=comp;jmp
     */
    public CommandType commandType() {
    	
    	if(currentCommand.charAt(0)=='@')
    		return CommandType.A_COMMAND;
    	
    	if(currentCommand.charAt(0)=='(')
    		return CommandType.L_COMMAND;
    	
    	return CommandType.C_COMMAND;
    }
    
    /**
     * Returns the symbol or decimal Xxx of the current command @Xxx or (Xxx). 
     * Should be called only when commandType() is A_COMMAND or L_COMMAND.
     * 
     * @return xxx if the current command is @xxx or (xxx)
     */
    public String symbol() {
    	
    	// should only be called for A_COMMAND and L_COMMAND
    	assert(commandType()==CommandType.A_COMMAND 
    			|| commandType()==CommandType.L_COMMAND);
    	
    	if(commandType()==CommandType.A_COMMAND) {
    		return currentCommand.replaceAll("[@]","");
    	}
    	
    	if(commandType()==CommandType.L_COMMAND) {
    		return currentCommand.replaceAll("[(|)]","");
    	}
    	
    	return null;
    }
    
    /**
     * Returns the dest mnemonic in
     * the current C-command (8 possibilities). 
     * Should be called only when commandType() is C_COMMAND.
     * 
     * @return dest if the current command is dest=comp;jmp
     *         null if the current command does not have dest mnemonic
     */
    public String dest() {
    	
    	assert(commandType()==CommandType.C_COMMAND);
    	
    	String dest = null;
    	
    	if(currentCommand.contains("=")) {
    		dest = currentCommand.replaceAll("=.*", "");
    	}
    	
    	return dest;
    }
    
    /**
     * Returns the comp mnemonic in the current C-command 
     * (28 possibilities). 
     * Should be called only when commandType() is C_COMMAND.
     * 
     * @return comp if currentCommand is dest=comp;jump
     */
    public String comp() {
    	
    	assert(commandType()==CommandType.C_COMMAND);
    	
    	// currentCommand surely contains the comp field
    	// We start with currentCommand and remove the 
    	// other fields
    	String comp = currentCommand;
    	
    	// remove the [dest=] part if present
    	if(comp.contains("=")) {
    		comp = comp.replaceAll(".*=","");
    	}
    	
    	// remove the [;jmp] part if present
    	if(comp.contains(";")) {
    		comp = comp.replaceAll(";.*","");
    	}
    	
    	return comp;
    } 
    
    /**
     * Returns the jump mnemonic in the current C-command 
     * (8 possibilities). 
     * Should be called only when commandType() is C_COMMAND.
     * 
     * @return jump if current command is dest=comp;jump
     *         null if jump mnemonic is not present in currentCommand
     */
    public String jump() {
    	
    	assert(commandType()==CommandType.C_COMMAND);
    	
    	String jump = null;
    	
    	if(currentCommand.contains(";")) {
    		jump = currentCommand.replaceAll(".*;", "");
    	}
    	
    	return jump;
    }
    
    /**
     * Closes input file
     */
    public void closeFile() {
    	try {
    		if(inputFile != null) {
            	inputFile.close();    		
        	} else {
        		System.err.println("Parser.closeFile: Input file not open");
        	}	
    	} catch (IOException e) {
    		System.err.println("Parser.closeFile: Caught IOException" + e.getMessage());
    	}
    }
}