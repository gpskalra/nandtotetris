package com.nandtotetris.jackcompiler;

import java.io.File;

/**
 *
 * @author gaganpreet1810@gmail.com
 */
public class JackCompiler {



    public static void main(String[] args) {

        if (args.length != 1) {

            System.out.println("Error: Expected one argument: <input file/dir name>");
            System.exit(1);

        }

        File input = new File(args[0]);

        Utils.validateInput(input);
        compile(input);
}
