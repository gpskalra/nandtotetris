package com.nandtotetris.jackcompiler;

import java.io.File;

/**
 *
 * @author gaganpreet1810@gmail.com
 */
public class JackCompiler {

    public static void main(String[] args) {

        File inputFile = new File(args[0]);
        JackTokenizer tokenizer = new JackTokenizer(inputFile);

        while(tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            System.out.println("Current Token = " + tokenizer.getCurrentToken());
        }
    }
}
