package com.nandtotetris.myassembler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Code {
	
	// Maps the comp mnemonic to its 7 bit binary code 
	private static final Map<String,String> compCodes;
	
	static {	
		
		Map<String,String> compCodesTemp = new HashMap<String,String>();
		
		compCodesTemp.put("0"  ,"0101010");
		compCodesTemp.put("1"  ,"0111111");
		compCodesTemp.put("-1" ,"0111010");
		compCodesTemp.put("D"  ,"0001100");
		compCodesTemp.put("A"  ,"0110000");
		compCodesTemp.put("M"  ,"1110000");
		compCodesTemp.put("!D" ,"0001101");
		compCodesTemp.put("!A" ,"0110001");
		compCodesTemp.put("!M" ,"1110001");
		compCodesTemp.put("-D" ,"0001111");
		compCodesTemp.put("-A" ,"0110011");
		compCodesTemp.put("-M" ,"1110011");
		compCodesTemp.put("D+1","0011111");
		compCodesTemp.put("A+1","0110111");
		compCodesTemp.put("M+1","1110111");
		compCodesTemp.put("D-1","0001110");
		compCodesTemp.put("A-1","0110010");
		compCodesTemp.put("M-1","1110010");
		compCodesTemp.put("D+A","0000010");
		compCodesTemp.put("D+M","1000010");
		compCodesTemp.put("D-A","0010011");
		compCodesTemp.put("D-M","1010011");
		compCodesTemp.put("A-D","0000111");
		compCodesTemp.put("M-D","1000111");
		compCodesTemp.put("D&A","0000000");
		compCodesTemp.put("D&M","1000000");
		compCodesTemp.put("D|A","0010101");
		compCodesTemp.put("D|M","1010101");
		
		compCodes = Collections.unmodifiableMap(compCodesTemp);
	}
	
	// Maps the jump mnemonic to its 3 bit binary code
	private static final Map<String,String> jumpCodes;
	
	static {
		
		Map<String,String> jumpCodesTemp = new HashMap<String,String>();
		
		jumpCodesTemp.put(null ,"000");
		jumpCodesTemp.put("JGT","001");
		jumpCodesTemp.put("JEQ","010");
		jumpCodesTemp.put("JGE","011");
		jumpCodesTemp.put("JLT","100");
		jumpCodesTemp.put("JNE","101");
		jumpCodesTemp.put("JLE","110");
		jumpCodesTemp.put("JMP","111");
		
		jumpCodes = Collections.unmodifiableMap(jumpCodesTemp);
	}
	
	/**
	 * returns the destination bits d1 d2 d3 of the binary 
	 * code of the C-instruction for the given dest mnemonic
	 *  
	 * @param destMnemonic the dest part of the C-instruction dest=comp;jump
	 * @return 3 bits d1 d2 d3 according to the following table
	 *                         d1 d2 d3 destmnemonic
	 *                         0  0  0  null
	 *                         0  0  1  M
	 *                         0  1  0  D
	 *                         0  1  1  MD
	 *                         1  0  0  A
	 *                         1  0  1  AM
	 *                         1  1  0  AD
	 *                         1  1  1  AMD
	 */
	public static String dest(String destMnemonic) {
		
		if(destMnemonic==null)
			return "000";
		
		StringBuilder destBits = new StringBuilder("000");
		
		if(destMnemonic.contains("A")) {
			destBits.setCharAt(0, '1');
		}
		
		if(destMnemonic.contains("D")) {
			destBits.setCharAt(1, '1');
		}
		
		if(destMnemonic.contains("M")) {
			destBits.setCharAt(2, '1');
		}
		
		return destBits.toString();
	}
	
	/**
	 * Returns the binary code of the comp mnemonic.
	 * @return 7 comp bits a c1 c2 c3 c4 c5 c6 
	 *                     as per the map compCodes
	 */
	public static String comp(String compMnemonic) {
		return compCodes.get(compMnemonic);
	} 
	
	/**
	 * Returns the binary code of the jump mnemonic 
	 * @param jumpMnemonic
	 * @return 3 jump bits j1 j2 j3 
	 *                     as per the map jumpCodes
	 */
	public static String jump(String jumpMnemonic) {
		return jumpCodes.get(jumpMnemonic);
	}
}