package com.nandtotetris.jackcompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * This class emits VM commands to an
 * output file.
 */
public class VMWriter {

    private PrintWriter outputWriter;

    public VMWriter(File outputFile) {
        try {
            outputWriter = new PrintWriter(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writePush(Segment segment,int index) {
        outputWriter.println("push " + segment.getString() + " " + index);
    }

    public void writePop(Segment segment,int index) {
        outputWriter.println("pop " + segment.getString() + " " + index);
    }

    public void writeArithmetic(ArithmeticCommand command) {
        outputWriter.println(command.getString());
    }

    public void writeLabel(String label) {
        outputWriter.println("label " + label);
    }

    public void writeGoto(String label) {
        outputWriter.println("goto " + label);
    }

    public void writeIf(String label) {
        outputWriter.println("if-goto " + label);
    }

    public void writeCall(String functionName, int numArgs) {
        outputWriter.println("call " + functionName + " " + numArgs);
    }

    public void writeFunction(String name,int numLocals) {
        outputWriter.println("function " + name + " " + numLocals);
    }

    public void writeReturn() {
        outputWriter.println("return");
    }

    public void close() {
        outputWriter.close();
    }
}
