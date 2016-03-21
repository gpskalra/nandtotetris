package com.nandtotetris.vmtranslator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Handles the parsing of a single vm file. A new object should be created
 * for each vm file. Gives access to various parts of a vm command
 * in the input file.
 *
 * @author gaganpreet1810@gmail.com
 */
public class Parser {

    public static final String READ_MODE = "r";

    // the command currently pointed to by the file pointer
    private String mCurrentCommand;

    // the input VM file to parse
    private RandomAccessFile mInputFile;

    /**
     * Opens an input vm file for parsing
     * @param file the File object corresponding to the
     *             file to open
     */
    public Parser(File file) {

        this(file.getAbsolutePath());

    }

    /**
     * Opens an input vm file for parsing.
     *
     * @param fileName the name of the input file to open.
     *                 The input file should have a .vm extension
     */
    public Parser(String fileName) {

        try {

            mInputFile = new RandomAccessFile(fileName,READ_MODE);
            mCurrentCommand = null;

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

    }


    /**
     * returns true if an input string is a valid VM command
     *
     * @param inputString the string to check
     * @return true  if the input string is a VM command
     *         false if not
     */
    public Boolean isAVMCommand(String inputString) {

        if(inputString == null)
            return false;

        String whitespaceRegex = "(\\s*)";
        String commentRegex = "(//.*)";

        // zero or more whitespaces followed by an optional comment
        String notA_CommandRegex = whitespaceRegex+commentRegex+"?";

        return !(inputString.matches(notA_CommandRegex));
    }

    /**
     * Returns true if the input file has more commands.
     *
     * @return true  if the input file has more commands
     *               looking ahead from the current file pointer
     *               location.
     *         false otherwise
     */
    public Boolean hasMoreCommands() {

        try {

            long currentFilePointer;

            while(true) {

                // save the current file pointer
                currentFilePointer = mInputFile.getFilePointer();

                String line = mInputFile.readLine();

                if(line == null)
                    return false;

                if(isAVMCommand(line)) {

                    // advance method should be able to access the command.
                    // move the pointer behind the line.
                    mInputFile.seek(currentFilePointer);

                    return true;
                }

            }

        } catch(IOException e) {

            System.out.println("Error: IO Exception while processing input vm file");
            e.printStackTrace();

            try {

                if(mInputFile != null) {
                    mInputFile.close();
                }

            } catch(IOException f) {
                System.out.println("Error: IO Exception while closing input file");
                f.printStackTrace();
            }

        }

        // should never reach this line
        return true;
    }

    /**
     * Advances in the input file. Sets the current command
     * to the command at the current file location.
     * Should be called only when hasMoreCommands returns true.
     */
    public void advance() {

        try {

            String line = mInputFile.readLine();
            String tailWhiteSpaceOrCommentRegex = "((\\s*)(//.*)?)$";
            mCurrentCommand = line.replaceAll(tailWhiteSpaceOrCommentRegex,"");

        } catch (IOException e) {

            e.printStackTrace();

            try {
                if (mInputFile != null)
                    mInputFile.close();
            } catch (IOException f) {
                f.printStackTrace();
            }
        }

    }

    /**
     * Evaluates the current command and
     * returns the type of the VM command.
     * advance must have been called so that the
     * current command is set.
     *
     * @return the type of the VM command
     */
    public CommandTypeVM commandType() {

        if (mCurrentCommand.contains("push"))
            return CommandTypeVM.C_PUSH;
        if (mCurrentCommand.contains("pop"))
            return CommandTypeVM.C_POP;

        if (mCurrentCommand.contains("label"))
            return CommandTypeVM.C_LABEL;
        if (mCurrentCommand.contains("if"))
            return CommandTypeVM.C_IF;
        if (mCurrentCommand.contains("goto"))
            return CommandTypeVM.C_GOTO;

        if (mCurrentCommand.contains("function"))
            return CommandTypeVM.C_FUNCTION;
        if (mCurrentCommand.contains("return"))
            return CommandTypeVM.C_RETURN;
        if (mCurrentCommand.contains("call"))
            return CommandTypeVM.C_CALL;

        return CommandTypeVM.C_ARITHMETIC;

    }

    /**
     * Returns the current command in the input file.
     * @return the private member mCurrentCommand
     */
    public String getCurrentCommand() {
        return mCurrentCommand;
    }

    /**
     * Returns the first argument of the current command.
     * In the case of C_ARITHMETIC, the command itself
     * (add, sub, etc.) is returned. Should not be called
     * if the current command is C_RETURN.
     *
     * @return the first argument of the VM command
     */
    public String arg1() {

        assert !commandType().equals(CommandTypeVM.C_RETURN);

        if(commandType()==CommandTypeVM.C_ARITHMETIC)
            return mCurrentCommand;

        String firstArgument;

        // remove the command name
        firstArgument = mCurrentCommand.replaceFirst("([a-z-]+)( )","");

        // remove second argument if present
        firstArgument = firstArgument.replaceAll("( )(\\d)+","");

        return firstArgument;
    }

    /**
     * Returns the second argument of the current command.
     * Should be called only if the current command is
     * C_PUSH, C_POP, C_FUNCTION, or C_CALL.
     *
     * @return x       if current command is push segment x
     *         x       if current command is pop segment x
     *         nLocals if current command is function functionName nLocals
     *         nArgs   if current command is call functionName nArgs
     */
    public int arg2() {

        assert commandType().equals(CommandTypeVM.C_PUSH) ||
                commandType().equals(CommandTypeVM.C_POP) ||
                commandType().equals(CommandTypeVM.C_FUNCTION) ||
                commandType().equals(CommandTypeVM.C_CALL);

        // extract the second argument from the VM command
        String secondArgument = mCurrentCommand.replaceAll("(.*)( )((\\d)+)","$3");

        return Integer.parseInt(secondArgument);
    }

    /**
     * Closes the input file opened for parsing
     */
    public void close() {
        try {
            if (mInputFile != null) {
               mInputFile.close();
            }
        } catch (IOException e) {
            System.out.println("Error: Could not close input file");
            e.printStackTrace();
        }
    }
}
