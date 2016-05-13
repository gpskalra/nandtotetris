package com.nandtotetris.jackcompiler;

import java.io.File;

/**
 *
 * @author gaganpreet1810@gmail.com
 */
public class JackCompiler {

    /**
     * Driver for compilation.
     *
     * For each input jack file, create a xml file
     * and a vm file. Invoke the
     * compilation engine to write xml and vm output.
     *
     * @param input The input File object. Input can
     *              be a jack file or a directory with
     *              one or more jack files.
     */
    private static void compile(File input) {

        if (input.isDirectory()) {
            File[] inputFiles = input.listFiles();
            for (File file:inputFiles) {
                if (Utils.isJackFile(file)) {
                    File outputVMFile = new File(file.getAbsolutePath().replaceAll(".jack",".vm"));
                    CompilationEngine engine = new CompilationEngine(file,outputVMFile);
                    engine.compileClass();
                }
            }
        }
        else {
            File outputVMFile = new File(input.getAbsolutePath().replaceAll(".jack",".vm"));
            CompilationEngine engine = new CompilationEngine(input,outputVMFile);
            engine.compileClass();
        }
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Error: Expected one argument: <input file/dir name>");
            System.exit(1);
        }

        File input = new File(args[0]);

        Utils.validateInput(input);
        compile(input);
    }
}
