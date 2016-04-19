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

            System.out.format("%20s",tokenizer.getCurrentToken());

            switch (tokenizer.tokenType()) {
                case TOKEN_IDENTIFIER:
                    System.out.format("%20s",tokenizer.tokenType().toString());
                    System.out.format("%20s",tokenizer.identifier());
                    System.out.println();
                    break;
                case TOKEN_SYMBOL:
                    System.out.format("%20s",tokenizer.tokenType().toString());
                    System.out.format("%20c",tokenizer.symbol());
                    System.out.println();
                    break;
                case TOKEN_INT_CONST:
                    System.out.format("%20s",tokenizer.tokenType().toString());
                    System.out.format("%20d",tokenizer.intVal());
                    System.out.println();
                    break;
                case TOKEN_STRING_CONST:
                    System.out.format("%20s",tokenizer.tokenType().toString());
                    System.out.format("%20s",tokenizer.stringVal());
                    System.out.println();
                    break;
                case TOKEN_KEYWORD:
                    System.out.format("%20s",tokenizer.tokenType().toString());
                    System.out.format("%20s",tokenizer.keyword().toString());
                    System.out.println();
                    break;
                default:
                    break;
            }
        }
    }
}
