package com.nandtotetris.vmtranslator;

import java.io.File;

public class VMTranslator {

    /**
     * Validates that a file has extension vm
     * @param file File object for the input file
     */
    public void validateExtension(File file) {

        String fileName = file.getAbsolutePath();
        String testExtensionPattern = "(.*)(\\.vm)";

        if(!fileName.matches(testExtensionPattern)) {
            System.out.println("Error: The input file does not have a .vm extension");
            System.out.println("Exiting");
            System.exit(1);
        }
    }

    /**
     * Checks if the files have necessary vm extensions
     * If the input corresponds to a directory, checks if all
     * files contained in it have vm extension. In case the
     * input is a file, checks that the file has vm extension.
     *
     * @param input the input File object
     */
    public void validateExtensions(File input) {

        if(input.isDirectory()) {

            File[] listOfInputFiles = input.listFiles();

            if(listOfInputFiles==null) {
                System.out.println("Error: The input directory is empty");
                System.out.println("Exiting");
                System.exit(1);
            }

            for(File file:listOfInputFiles) {
                validateExtension(file);
            }

        } else {
            validateExtension(input);
        }

    }

    /**
     * Returns the file object for the output file
     * by appending .asm to the input dir/file.
     *
     * @param input File object for the input file/dir
     * @return File object for the output file
     */
    File getOutputFile(File input) {

        File outputFile;

        if(input.isDirectory()) {

            outputFile = new File(input.getAbsolutePath().concat(".asm"));

        } else {

            outputFile = new File(input.getAbsolutePath().replaceAll("\\.vm",".asm"));

        }

        return outputFile;

    }

    /**
     * For a given input vm file and codewriter object,
     * writes the hack assembly code for the
     * input vm file in the file associated
     * with the codewriter object
     *
     * @param inputFile the input vm file
     * @param codeWriter CodeWriter object to use to write the
     *                   output code
     */
    public void translateFile(File inputFile, CodeWriter codeWriter) {

        codeWriter.setCurrentFile(inputFile);
        codeWriter.emitComment("file: " + inputFile.getName());

        Parser parser = new Parser(inputFile);

        while(parser.hasMoreCommands()) {

            parser.advance();

            // Write the command as a comment
            // Helps in debugging
            codeWriter.emitComment(parser.getCurrentCommand());

            switch (parser.commandType()) {

                case C_ARITHMETIC:

                    codeWriter.writeArithmetic(parser.arg1());
                    break;

                case C_PUSH:
                case C_POP:

                    codeWriter.writePushPop(parser.commandType(),parser.arg1(),parser.arg2());
                    break;

                case C_CALL:

                    codeWriter.writeCall(parser.arg1(),parser.arg2());
                    break;

                case C_FUNCTION:

                    codeWriter.setCurrentFunction(parser.arg1());
                    codeWriter.writeFunction(parser.arg1(),parser.arg2());
                    break;

                case C_RETURN:

                    codeWriter.writeReturn();
                    break;

                case C_LABEL:

                    codeWriter.writeLabel(parser.arg1());
                    break;

                case C_GOTO:

                    codeWriter.writeGoto(parser.arg1());
                    break;

                case C_IF:

                    codeWriter.writeIf(parser.arg1());
                    break;

                default:

                    break;

            }

        }

        parser.close();

    }

    /**
     * Translates and writes the hack code for
     * a given input directory or vm file
     *
     * @param input the File object for the input
     *              directory or input vm file
     */
    public void translate(File input) {

        validateExtensions(input);
        File outputFile = getOutputFile(input);

        CodeWriter codeWriter = new CodeWriter(outputFile);

        codeWriter.writeInit();

        if (input.isDirectory()) {

            // list of files to process
            File[] listOfInputFiles = input.listFiles();

            if(listOfInputFiles==null) {
                System.out.println("Error: The input directory is empty");
                System.out.println("Exiting");
                System.exit(1);
            }

            // translate each file in the directory
            for(File file:listOfInputFiles) {
                translateFile(file,codeWriter);
            }

        } else {
            translateFile(input,codeWriter);
        }

        codeWriter.close();

    }

    public static void main(String[] args) {

        if(args.length != 1) {

            System.out.println("Error: Expected one argument: <input file/dir name>");
            System.exit(1);

        }

        File input = new File(args[0]);

        VMTranslator translator = new VMTranslator();
        translator.translate(input);

    }

}
