package com.nandtotetris.vmtranslator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Writes hack assembly code to an output file.
 * Contains methods to handle each vm command individually.
 * These methods begin with "write...". There is a method for
 * each vm command.
 *
 * The output is written using 4 "emit..." methods, one for writing
 * comments and one each for hack C,A,L commands.
 *
 * @author gaganpreet1810@gmail.com
 */
public class CodeWriter {

    private PrintWriter mOutputFile;

    private String mCurrentFileName;

    private String mCurrentFunctionName;

    // A running count of the number of logical vm commands
    // (eq,gt,lt) encountered. This is used to define unique
    // labels to jump out of the hack assembly code corresponding
    // to the logical command.
    private int mComparisonCommandCount;

    // A running count of the number of function calls
    // made up to this point
    private int mFunctionCallCount;

    /**
     * Opens the output file and gets
     * ready to write into it.
     *
     * @param file the file object corresponding
     *             to the output file
     */
    public CodeWriter(File file) {

        try {

            mOutputFile = new PrintWriter(file);
            mComparisonCommandCount = 0;
            mFunctionCallCount = 0;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Informs the code writer that the translations
     * of a new VM file is started.
     *
     * @param newVMFile the file object for the new
     *                  vm file
     */
    public void setCurrentFile(File newVMFile) {
        mCurrentFileName = newVMFile.getName().replaceAll(".vm","");
    }

    /**
     * Used by label, goto and if-goto commands to append function
     * name to the label.
     * @param functionName the name of the function
     */
    public void setCurrentFunction(String functionName) {
        mCurrentFunctionName = functionName;
    }

    /**
     * Emits a comment to the output file
     * @param commentString the string to be written as a comment
     */
    public void emitComment(String commentString) {

        mOutputFile.println("// " + commentString);
        mOutputFile.flush();
    }

    /**
     * Writes a hack label declaration to the output file
     *
     * @param label the label "l" in the label declaration "(l)"
     */
    public void emitInstructionL(String label) {

        mOutputFile.println("(" + label + ")");
        mOutputFile.flush();

    }

    /**
     * Writes a C instruction to the output file.
     * Either the dest or the jump field may be empty.
     *
     * @param dest the dest field of the instruction.
     *
     * @param comp the comp field of the instruction.
     *
     * @param jump the jump field of the instruction.
     */
    public void emitInstructionC(String dest,String comp,String jump) {

        String line = "";

        if(!dest.isEmpty()) {
            line = line + dest + "=";
        }

        line = line + comp;

        if(!jump.isEmpty()) {
            line = line + ";" + jump;
        }

        mOutputFile.println(line);
        mOutputFile.flush();
    }

    /**
     * Writes an A instruction @argument to the output file
     *
     * @param argument the argument to the A instruction @argument
     */
    public void emitInstructionA(String argument) {

        String line = "@" + argument;
        mOutputFile.println(line);
        mOutputFile.flush();

    }

    /**
     * Writes assembly code that effects the VM initialisation, also called
     * bootstrap code. Placed at the beginning of the output file.
     */
    public void writeInit() {

        // SP=256
        mOutputFile.println("@256");
        mOutputFile.println("D=A");
        mOutputFile.println("@SP");
        mOutputFile.println("M=D");

        // set LCL, ARG, THIS, THAT
        // to known illegal values

        // LCL=-1
        mOutputFile.println("@0");
        mOutputFile.println("D=A");
        mOutputFile.println("@1");
        mOutputFile.println("D=D-A");
        mOutputFile.println("@LCL");
        mOutputFile.println("M=D");

        // ARG=-2
        mOutputFile.println("@0");
        mOutputFile.println("D=A");
        mOutputFile.println("@2");
        mOutputFile.println("D=D-A");
        mOutputFile.println("@ARG");
        mOutputFile.println("M=D");

        // THIS=-3
        mOutputFile.println("@0");
        mOutputFile.println("D=A");
        mOutputFile.println("@3");
        mOutputFile.println("D=D-A");
        mOutputFile.println("@THIS");
        mOutputFile.println("M=D");

        // THAT=-4
        mOutputFile.println("@0");
        mOutputFile.println("D=A");
        mOutputFile.println("@4");
        mOutputFile.println("D=D-A");
        mOutputFile.println("@THAT");
        mOutputFile.println("M=D");

        mOutputFile.flush();

        // call Sys.init (defined in the file Sys.vm)
        writeCall("Sys.init",0);

    }

    /**
     * Writes the assembly code that is the translation of
     * the given arithmetic command.
     *
     * @param command the arithmetic vm command to translate to
     *                hack code.
     */
    public void writeArithmetic(String command) {

        switch(command) {

            case "add" :

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("D=M");

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // M[SP]=M[SP]+D
                mOutputFile.println("A=M");
                mOutputFile.println("M=D+M");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                break;

            case "sub" :

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("D=M");

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // M[SP]=M[SP]-D
                mOutputFile.println("A=M");
                mOutputFile.println("M=M-D");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                break;

            case "neg" :

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // M[SP]=-M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("M=-M");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                break;

            // A simpler way to implement eq,gt,lt instructions is to
            // use the stack, temp segment and boolean operations neg,
            // and, or. That approach leads to a lot more assembly
            // instructions in the output code. Remember each assembly
            // instructions consumes one clock cycle.

            // The jump mechanism currently uses a copy of the same code
            // to push true and false values on the stack. This can be changed
            // to use a call command to call a function to push the same.

            case "eq" :

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("D=M");

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]-D
                mOutputFile.println("A=M");
                mOutputFile.println("D=M-D");


                // if D==0, M[SP] = 0xFFFF (true)
                mOutputFile.println("@PUSH_TRUE_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("D;JEQ");

                // otherwise, M[SP] = 0x0 (false)
                mOutputFile.println("@PUSH_FALSE_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("0;JMP");


                // label (PUSH_TRUE_ + <LOGICAL_COMMAND_COUNT>)
                // makes M[SP] = 0xFFFF, increments SP and leaves
                // leave label = LEAVE_COMPARISON_COMMAND_ + <COMPARISON_COMMAND_COUNT>
                mOutputFile.println("(PUSH_TRUE_" + Integer.toString(mComparisonCommandCount) + ")");
                mOutputFile.println("@SP");
                mOutputFile.println("A=M");
                mOutputFile.println("M=-1");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                // leave
                mOutputFile.println("@LEAVE_COMPARISON_COMMAND_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("0;JMP");

                // label (PUSH_FALSE_ + <LOGICAL_COMMAND_COUNT>)
                // makes M[SP] = 0x0, increments SP and leaves
                // leave label = LEAVE_COMPARISON_COMMAND_ + <COMPARISON_COMMAND_COUNT>
                mOutputFile.println("(PUSH_FALSE_" + Integer.toString(mComparisonCommandCount) + ")");
                mOutputFile.println("@SP");
                mOutputFile.println("A=M");
                mOutputFile.println("M=0");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                // leave
                mOutputFile.println("@LEAVE_COMPARISON_COMMAND_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("0;JMP");


                // leave label
                mOutputFile.println("(LEAVE_COMPARISON_COMMAND_" + Integer.toString(mComparisonCommandCount) + ")");

                mComparisonCommandCount = mComparisonCommandCount + 1;

                break;

            case "gt" :

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("D=M");

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]-D
                mOutputFile.println("A=M");
                mOutputFile.println("D=M-D");

                // if D>0, M[SP] = 0xFFFF (true)
                mOutputFile.println("@PUSH_TRUE_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("D;JGT");

                // otherwise, M[SP] = 0x0 (false)
                mOutputFile.println("@PUSH_FALSE_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("0,JMP");

                // label (PUSH_TRUE_ + <COMPARISON_COMMAND_COUNT>)
                // makes M[SP] = 0xFFFF, increments SP and leaves
                // leave label = LEAVE_COMPARISON_COMMAND_ + <COMPARISON_COMMAND_COUNT>
                mOutputFile.println("(PUSH_TRUE_" + Integer.toString(mComparisonCommandCount) + ")");
                mOutputFile.println("@SP");
                mOutputFile.println("A=M");
                mOutputFile.println("M=-1");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                // leave
                mOutputFile.println("@LEAVE_COMPARISON_COMMAND_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("0;JMP");

                // label (PUSH_FALSE_ + <LOGICAL_COMMAND_COUNT>)
                // makes M[SP] = 0x0, increments SP and leaves
                // leave label = LEAVE_COMPARISON_COMMAND_ + <COMPARISON_COMMAND_COUNT>
                mOutputFile.println("(PUSH_FALSE_" + Integer.toString(mComparisonCommandCount) + ")");
                mOutputFile.println("@SP");
                mOutputFile.println("A=M");
                mOutputFile.println("M=0");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                // leave
                mOutputFile.println("@LEAVE_COMPARISON_COMMAND_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("0;JMP");
                
                // leave label
                mOutputFile.println("(LEAVE_COMPARISON_COMMAND_" + Integer.toString(mComparisonCommandCount) + ")");

                mComparisonCommandCount = mComparisonCommandCount + 1;

                break;

            case "lt" :

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("D=M");

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]-D
                mOutputFile.println("A=M");
                mOutputFile.println("D=M-D");

                // if D<0, M[SP] = 0xFFFF (true)
                mOutputFile.println("@PUSH_TRUE_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("D;JLT");

                // otherwise, M[SP] = 0x0 (false)
                mOutputFile.println("@PUSH_FALSE_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("0,JMP");

                // label (PUSH_TRUE_ + <COMPARISON_COMMAND_COUNT>)
                // makes M[SP] = 0xFFFF, increments SP and leaves
                // leave label = LEAVE_COMPARISON_COMMAND_ + <COMPARISON_COMMAND_COUNT>
                mOutputFile.println("(PUSH_TRUE_" + Integer.toString(mComparisonCommandCount) + ")");
                mOutputFile.println("@SP");
                mOutputFile.println("A=M");
                mOutputFile.println("M=-1");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                // leave
                mOutputFile.println("@LEAVE_COMPARISON_COMMAND_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("0;JMP");

                // label (PUSH_FALSE_ + <LOGICAL_COMMAND_COUNT>)
                // makes M[SP] = 0x0, increments SP and leaves
                // leave label = LEAVE_COMPARISON_COMMAND_ + <COMPARISON_COMMAND_COUNT>
                mOutputFile.println("(PUSH_FALSE_" + Integer.toString(mComparisonCommandCount) + ")");
                mOutputFile.println("@SP");
                mOutputFile.println("A=M");
                mOutputFile.println("M=0");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                // leave
                mOutputFile.println("@LEAVE_COMPARISON_COMMAND_" + Integer.toString(mComparisonCommandCount));
                mOutputFile.println("0;JMP");

                // leave label
                mOutputFile.println("(LEAVE_COMPARISON_COMMAND_" + Integer.toString(mComparisonCommandCount) + ")");

                mComparisonCommandCount = mComparisonCommandCount + 1;

                break;

            case "and" :

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("D=M");

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // M[SP]= D&M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("M=D&M");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                break;

            case "or" :

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // D=M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("D=M");

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // M[SP]= D|M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("M=D|M");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                break;

            case "not" :

                // SP=SP-1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M-1");

                // M[SP]=!M[SP]
                mOutputFile.println("A=M");
                mOutputFile.println("M=!M");

                // SP=SP+1
                mOutputFile.println("@SP");
                mOutputFile.println("M=M+1");

                break;

        }

        mOutputFile.flush();

    }

    /**
     * Writes the assembly code that is the translation of
     * the given command, where command is either C_PUSH or C_POP.
     *
     * @param commandTypeVM the input command type, should be
     *                      C_PUSH or C_POP
     * @param segment       the memory segment argument of the command
     * @param index         the index argument of the command
     */
    public void writePushPop(CommandTypeVM commandTypeVM, String segment, int index) {

        switch (commandTypeVM) {

            case C_PUSH:

                switch (segment) {

                    case "constant" :

                        // M[SP]=index
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("D=A");
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("M=D");

                        // SP=SP+1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M+1");

                        break;

                    case "local" :

                        // D=M[LCL+index]
                        mOutputFile.println("@LCL");
                        mOutputFile.println("D=M");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("A=D+A");
                        mOutputFile.println("D=M");

                        // M[SP]=D
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("M=D");

                        // SP=SP+1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M+1");

                        break;

                    case "argument" :

                        // D=M[ARG+index]
                        mOutputFile.println("@ARG");
                        mOutputFile.println("D=M");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("A=D+A");
                        mOutputFile.println("D=M");

                        // M[SP]=D
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("M=D");

                        // SP=SP+1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M+1");

                        break;

                    case "this" :

                        // D=M[THIS+index]
                        mOutputFile.println("@THIS");
                        mOutputFile.println("D=M");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("A=D+A");
                        mOutputFile.println("D=M");

                        // M[SP]=D
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("M=D");

                        // SP=SP+1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M+1");

                        break;

                    case "that" :

                        // D=M[THAT+index]
                        mOutputFile.println("@THAT");
                        mOutputFile.println("D=M");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("A=D+A");
                        mOutputFile.println("D=M");

                        // M[SP]=D
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("M=D");

                        // SP=SP+1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M+1");

                        break;

                    case "pointer" :

                        // D=M[3+index]
                        mOutputFile.println("@R3");
                        mOutputFile.println("D=A");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("A=D+A"); // A=3+index
                        mOutputFile.println("D=M");

                        // M[SP]=D
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("M=D");

                        // SP=SP+1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M+1");

                        break;

                    case "temp" :

                        // D=M[5+index]
                        mOutputFile.println("@R5");
                        mOutputFile.println("D=A");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("A=D+A"); // A=5+index
                        mOutputFile.println("D=M");

                        // M[SP]=D
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("M=D");

                        // SP=SP+1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M+1");

                        break;

                    case "static" :

                        // D=M[filename.index]
                        mOutputFile.println("@" + mCurrentFileName + "." + Integer.toString(index));
                        mOutputFile.println("D=M");

                        // M[SP]=D
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("M=D");

                        // SP=SP+1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M+1");

                        break;

                }

                break;

            case C_POP:

                switch (segment) {

                    case "local" :

                        // D=LCL+index
                        mOutputFile.println("@LCL");
                        mOutputFile.println("D=M");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("D=D+A");

                        // R13=D
                        mOutputFile.println("@R13");
                        mOutputFile.println("M=D");

                        // SP=SP-1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M-1");

                        // D=M[SP]
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("D=M");

                        // A=R13
                        mOutputFile.println("@R13");
                        mOutputFile.println("A=M");

                        // M[LCL+index]=D
                        mOutputFile.println("M=D");

                        break;

                    case "argument" :

                        // D=ARG+index
                        mOutputFile.println("@ARG");
                        mOutputFile.println("D=M");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("D=D+A");

                        // R13=D
                        mOutputFile.println("@R13");
                        mOutputFile.println("M=D");

                        // SP=SP-1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M-1");

                        // D=M[SP]
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("D=M");

                        // A=R13
                        mOutputFile.println("@R13");
                        mOutputFile.println("A=M");

                        // M[ARG+index]=D
                        mOutputFile.println("M=D");

                        break;

                    case "this" :

                        // D=THIS+index
                        mOutputFile.println("@THIS");
                        mOutputFile.println("D=M");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("D=D+A");

                        // R13=D
                        mOutputFile.println("@R13");
                        mOutputFile.println("M=D");

                        // SP=SP-1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M-1");

                        // D=M[SP]
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("D=M");

                        // A=R13
                        mOutputFile.println("@R13");
                        mOutputFile.println("A=M");

                        // M[THIS+index]=D
                        mOutputFile.println("M=D");

                        break;

                    case "that" :

                        // D=THAT+index
                        mOutputFile.println("@THAT");
                        mOutputFile.println("D=M");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("D=D+A");

                        // R13=D
                        mOutputFile.println("@R13");
                        mOutputFile.println("M=D");

                        // SP=SP-1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M-1");

                        // D=M[SP]
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("D=M");

                        // A=R13
                        mOutputFile.println("@R13");
                        mOutputFile.println("A=M");

                        // M[THAT+index]=D
                        mOutputFile.println("M=D");

                        break;

                    case "pointer" :

                        // D=3+index
                        mOutputFile.println("@R3");
                        mOutputFile.println("D=A");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("D=D+A");

                        // R13=D
                        mOutputFile.println("@R13");
                        mOutputFile.println("M=D");

                        // SP=SP-1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M-1");

                        // D=M[SP]
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("D=M");

                        // A=R13
                        mOutputFile.println("@R13");
                        mOutputFile.println("A=M");

                        // M[3+index]=D
                        mOutputFile.println("M=D");

                        break;

                    case "temp" :

                        // D=5+index
                        mOutputFile.println("@R5");
                        mOutputFile.println("D=A");
                        mOutputFile.println("@"+Integer.toString(index));
                        mOutputFile.println("D=D+A");

                        // R13=D
                        mOutputFile.println("@R13");
                        mOutputFile.println("M=D");

                        // SP=SP-1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M-1");

                        // D=M[SP]
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("D=M");

                        // A=R13
                        mOutputFile.println("@R13");
                        mOutputFile.println("A=M");

                        // M[5+index]=D
                        mOutputFile.println("M=D");

                        break;

                    case "static" :

                        // SP=SP-1
                        mOutputFile.println("@SP");
                        mOutputFile.println("M=M-1");

                        // D=M[SP]
                        mOutputFile.println("@SP");
                        mOutputFile.println("A=M");
                        mOutputFile.println("D=M");

                        // M[filename.index]=D
                        mOutputFile.println("@" + mCurrentFileName + "." + Integer.toString(index));
                        mOutputFile.println("M=D");

                        break;

                }

                break;

        }

        mOutputFile.flush();

    }

    /**
     * Writes the assembly code that effects the call command.
     *
     * @param functionName The name of the function to call.
     *
     * @param numArgs The number of arguments the function takes.
     *                The VM code must push the arguments to the stack
     *                prior to a call command.
     */
    public void writeCall(String functionName, int numArgs) {

        // 1. push return address to stack
        String returnLabel = "RETURN_" + functionName + "_" + Integer.toString(mFunctionCallCount);

        // D=return address
        mOutputFile.println("@"+ returnLabel);
        mOutputFile.println("D=A");

        // M[SP]=D
        mOutputFile.println("@SP");
        mOutputFile.println("A=M");
        mOutputFile.println("M=D");

        // SP=SP+1
        mOutputFile.println("@SP");
        mOutputFile.println("M=M+1");

        // 2. push LCL
        mOutputFile.println("@LCL");
        mOutputFile.println("D=M");

        // M[SP]=D
        mOutputFile.println("@SP");
        mOutputFile.println("A=M");
        mOutputFile.println("M=D");

        // SP=SP+1
        mOutputFile.println("@SP");
        mOutputFile.println("M=M+1");

        // 3. push ARG
        mOutputFile.println("@ARG");
        mOutputFile.println("D=M");

        // M[SP]=D
        mOutputFile.println("@SP");
        mOutputFile.println("A=M");
        mOutputFile.println("M=D");

        // SP=SP+1
        mOutputFile.println("@SP");
        mOutputFile.println("M=M+1");

        // 4. push THIS
        mOutputFile.println("@THIS");
        mOutputFile.println("D=M");

        // M[SP]=D
        mOutputFile.println("@SP");
        mOutputFile.println("A=M");
        mOutputFile.println("M=D");

        // SP=SP+1
        mOutputFile.println("@SP");
        mOutputFile.println("M=M+1");

        // 5. push THAT
        mOutputFile.println("@THAT");
        mOutputFile.println("D=M");

        // M[SP]=D
        mOutputFile.println("@SP");
        mOutputFile.println("A=M");
        mOutputFile.println("M=D");

        // SP=SP+1
        mOutputFile.println("@SP");
        mOutputFile.println("M=M+1");

        // 6. ARG=SP-numArgs-5

        // D=numArgs+5
        mOutputFile.println("@" + Integer.toString(numArgs+5));
        mOutputFile.println("D=A");

        // D=SP-(numArgs+5)
        mOutputFile.println("@SP");
        mOutputFile.println("D=M-D");

        // ARG=D
        mOutputFile.println("@ARG");
        mOutputFile.println("M=D");

        // 7. LCL=SP

        // D=SP
        mOutputFile.println("@SP");
        mOutputFile.println("D=M");

        // LCL=D
        mOutputFile.println("@LCL");
        mOutputFile.println("M=D");

        // 8. transfer control to function
        mOutputFile.println("@" + functionName);
        mOutputFile.println("0;JMP");

        // 9. return label
        mOutputFile.println("(" + returnLabel + ")");

        mFunctionCallCount = mFunctionCallCount + 1;

        mOutputFile.flush();
    }

    /**
     * Writes assembly code that effects the function command
     *
     * @param functionName The name of the function declared.
     *                     It is always of the form
     *                     fileName.UnqualifiedFunctionName
     * @param numLocals The number of local variables in the function
     */
    public void writeFunction(String functionName,int numLocals) {

        mOutputFile.println("(" + functionName + ")");
        mOutputFile.flush();

        for (int i=0;i<numLocals;i++) {
            writePushPop(CommandTypeVM.C_PUSH,"constant",0);
        }

    }

    /**
     * Writes assembly code that effects the return command.
     */
    public void writeReturn() {

        // R13=M[LCL-5] (return address)
        mOutputFile.println("@5");
        mOutputFile.println("D=A");
        mOutputFile.println("@LCL");
        mOutputFile.println("A=M-D");
        mOutputFile.println("D=M");
        mOutputFile.println("@R13");
        mOutputFile.println("M=D");

        // push return value to stack

        // SP=SP-1
        mOutputFile.println("@SP");
        mOutputFile.println("M=M-1");

        // D=M[SP]
        mOutputFile.println("@SP");
        mOutputFile.println("A=M");
        mOutputFile.println("D=M");

        // M[ARG]=D
        mOutputFile.println("@ARG");
        mOutputFile.println("A=M");
        mOutputFile.println("M=D");

        // SP=ARG+1
        mOutputFile.println("@ARG");
        mOutputFile.println("D=M+1");
        mOutputFile.println("@SP");
        mOutputFile.println("M=D");

        // restore THAT,THIS,ARG,LCL

        // LCL=LCL-1
        mOutputFile.println("@LCL");
        mOutputFile.println("M=M-1");

        // THAT = M[LCL]
        mOutputFile.println("@LCL");
        mOutputFile.println("A=M");
        mOutputFile.println("D=M");

        mOutputFile.println("@THAT");
        mOutputFile.println("M=D");

        // LCL=LCL-1
        mOutputFile.println("@LCL");
        mOutputFile.println("M=M-1");

        // THIS = M[LCL]
        mOutputFile.println("@LCL");
        mOutputFile.println("A=M");
        mOutputFile.println("D=M");

        mOutputFile.println("@THIS");
        mOutputFile.println("M=D");

        // LCL=LCL-1
        mOutputFile.println("@LCL");
        mOutputFile.println("M=M-1");

        // ARG = M[LCL]
        mOutputFile.println("@LCL");
        mOutputFile.println("A=M");
        mOutputFile.println("D=M");

        mOutputFile.println("@ARG");
        mOutputFile.println("M=D");

        // LCL=LCL-1
        mOutputFile.println("@LCL");
        mOutputFile.println("M=M-1");

        // LCL=M[LCL]
        mOutputFile.println("@LCL");
        mOutputFile.println("A=M");
        mOutputFile.println("D=M");

        mOutputFile.println("@LCL");
        mOutputFile.println("M=D");

        // jump to R13
        mOutputFile.println("@R13");
        mOutputFile.println("A=M");
        mOutputFile.println("0;JMP");

        mOutputFile.flush();
    }

    /**
     * Writes assembly code that effects the label command
     * The label must be defined in the same function. We enforce
     * this condition by pre-pending the function name to the label.
     *
     * @param label the input label string
     */
    public void writeLabel(String label) {

        String labelQualifiedWithFunctionName = mCurrentFunctionName + "$" + label;

        mOutputFile.println("(" + labelQualifiedWithFunctionName + ")");
        mOutputFile.flush();

    }

    /**
     * Writes assembly code that effects the goto command.
     * We prepend the label with the function name since the label
     * command generates such labels.
     *
     * @param label the label string.
     */
    public void writeGoto(String label) {

        String labelQualifiedWithFunctionName = mCurrentFunctionName + "$" + label;

        mOutputFile.println("@"+labelQualifiedWithFunctionName);
        mOutputFile.println("0;JMP");

        mOutputFile.flush();
    }

    /**
     * Writes assembly code that effects the if-goto command.
     * We prepend the label with the function name since the label
     * command generates such labels.
     *
     * @param label the label string
     */
    public void writeIf(String label) {

        String labelQualifiedWithFunctionName = mCurrentFunctionName + "$" + label;

        // SP=SP-1
        mOutputFile.println("@SP");
        mOutputFile.println("M=M-1");

        // D=M[SP]
        mOutputFile.println("@SP");
        mOutputFile.println("A=M");
        mOutputFile.println("D=M");

        // if D != 0, jump to label
        mOutputFile.println("@" + labelQualifiedWithFunctionName);
        mOutputFile.println("D;JNE");

        mOutputFile.flush();

    }

    /**
     * Closes the output file.
     */
    public void close() {

        if(mOutputFile!=null) {
            mOutputFile.close();
        }

    }
}
