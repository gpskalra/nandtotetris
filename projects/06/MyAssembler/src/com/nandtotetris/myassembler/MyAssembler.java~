package com.nandtotetris.myassembler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MyAssembler {
    
	private SymbolTable symbolTable;
	
	/**
	 * Opens a new .hack file corresponding to the input .asm file
	 * @param inputFileName filename of the .asm file to open
	 */
	public MyAssembler() {
		
		symbolTable = new SymbolTable();
	}		
	
	/**
	 * converts an input decimal number (as a string) to binary
	 * 
	 * @param number The decimal number (as a string) to convert 
	 * @return 15 bit binary number as a string
	 */
	private String decimalToBinary(String numberString) {
		
		// System.out.println("numberString = " + numberString);
		String result = null;
		int number = Integer.parseInt(numberString);
		String binaryNumberString = Integer.toBinaryString(number);
			
		// System.out.println("Binary Number String = " + binaryNumberString);
		
		// pad with 0's to make the length 15
		result = String.format("%015d", Long.parseLong(binaryNumberString)); 	
		
		return result;
	}
		
	/**
	 *  Runs through the input file caring only for L-instructions
	 *  Adds <symbol,address> to the symbol table
	 *  where address is the number of the instruction following the 
	 *  L-instruction 
	 */
	private void firstPass(String inputFileName) {

		Parser parser = null;
		
		try{
			// opens input file for parsing
			parser = new Parser(inputFileName);
		
			// A running integer to store the instruction number
			Integer nInstruction = 0;
			
			while(parser.hasMoreCommands()) {
				
				// set currentCommand
				parser.advance();
				
				// Add the symbol and the instruction number of the next line
				// to the symbol table
				if(parser.commandType()==CommandType.L_COMMAND) {
					symbolTable.addEntry(parser.symbol(), nInstruction);
				}
				else {
					
					// It is an A-instruction or a C-instruction 
					nInstruction = nInstruction + 1;
				}
			}
		} catch (IOException e) {
			System.err.println("Caught IOException: " +  e.getMessage());
		} finally {
			// close input file
			parser.closeFile();
		}
	}
	
	/**
	 * Runs through the input file.
	 * For each instruction, writes the corresponding binary code to the 
	 * .hack file
	 */
	private void secondPass(String inputFileName) {

		PrintWriter outputFile = null;
		
		String outputFileName = inputFileName.replaceAll(".asm",".hack");
		
		try {
			outputFile = new PrintWriter(new FileWriter(outputFileName));
		
			// opens the input file for parsing
			Parser parser = new Parser(inputFileName);
			
			Integer variableAddress = 16;
			
			while(parser.hasMoreCommands()) {
				
				// set currentCommand
				parser.advance();
				
				String instruction = null;
			
				switch (parser.commandType()) {
				
				case A_COMMAND:
					
					String address = null;
					String integerRegex = "\\d+";
					
					if(parser.symbol().matches(integerRegex)) {
						address = parser.symbol();
					}
					else {
						String symbol = parser.symbol();
						if(symbolTable.contains(symbol)) {
							address = symbolTable.getAddress(symbol).toString();
						} 
						else {
							symbolTable.addEntry(symbol, variableAddress);
							address = variableAddress.toString();
							variableAddress = variableAddress + 1;
						}
					}
					
					instruction    = "0" + decimalToBinary(address);
					
					// Write instruction to file
					outputFile.println(instruction);
					
					break;
					
				case C_COMMAND:
					
					instruction = "111" + Code.comp(parser.comp()) + Code.dest(parser.dest()) + Code.jump(parser.jump());
					
					// Write instruction to file
					outputFile.println(instruction);
					
					break;
					
				case L_COMMAND:
				default:
					
					break;
				}
			}
		} catch(IOException e) {
			System.err.println("Caught IOException: " +  e.getMessage());
		} finally {
			outputFile.close();
		}
	}
	
	/**
	 * Assembles the input file and writes output to .hack output file
	 * 
	 * @param inputFileName: .asm input file name
	 */
	private void assemble(String inputFileName) {
		
		// Add predefined symbols to the symbol table
		symbolTable.addPreDefinedSymbols();
		
		// First pass of the assembling process
		firstPass(inputFileName);
		
		// Second pass of the assembling process
		secondPass(inputFileName);
	}

	public static void main(String[] args) throws IOException {
		
		String inputFileName = null;
		
		if(args.length != 1) {
			System.err.println("Single input argument <inputfile> expected");
			System.exit(1);
		}
		else {
			inputFileName = args[0];
		}
		
		
		// Initializes an empty symbol table for the assembling process
		MyAssembler assembler = new MyAssembler();
		
		assembler.assemble(inputFileName);
	}	
}
