package com.nandtotetris.vmtranslator;

import java.io.File;

public class VMTranslator {

    /**
     * returns true if an input file has extension vm
     *
     * @param file File object for the input file
     */
    public boolean isAVmFile(File file) {

        String fileName = file.getAbsolutePath();
        String testExtensionPattern = "(.*)(\\.vm)";

        return fileName.matches(testExtensionPattern);
    }

    /**
     * Validates the input and returns the file object for
     * the output file.
     *
     * If the input corresponds to a directory, checks if
     * it contains at least one vm file. In case the
     * input is a file, checks that the file has vm extension.
     * If any check fails, it exits the program.
     *
     * If the input is a directory, appends ".asm" to the dir
     * name to get the output file name. If the input is a file,
     * replaces ".vm" with ".asm"
     *
     * @param input the input File object
     * @return File object for the output file
     */
    public File validateInputAndGetOutputFile(File input) {

        File outputFile;

        if(input.isDirectory()) {

            File[] listOfInputFiles = input.listFiles();

            boolean seenAVmFile = false;
            for(File file:listOfInputFiles) {

                if (isAVmFile(file)) {
                    seenAVmFile = true;
                    break;
                }

            }

            if (!seenAVmFile) {
                System.out.println("Error: The input directory does not have a vm file");
                System.out.println("Exiting");
                System.exit(1);
            }

            outputFile = new File(input.getAbsolutePath() + File.separator + input.getName() + ".asm");

        }

        else {

            if (!isAVmFile(input)) {
                System.out.println("Error: The input file does not have a vm extension");
                System.out.println("Exiting");
                System.exit(1);
            }

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

        if(isAVmFile(inputFile)) {

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

    }

    /**
     * Translates and writes the hack code for
     * a given input directory or vm file. Called
     * from main after it creates a file object
     * for the input.
     *
     * @param input the File object for the input
     *              directory or input vm file
     */
    public void translate(File input) {

        File outputFile = validateInputAndGetOutputFile(input);

        CodeWriter codeWriter = new CodeWriter(outputFile);

        codeWriter.writeInit();

        if (input.isDirectory()) {

            // list of files to process
            File[] listOfInputFiles = input.listFiles();

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
