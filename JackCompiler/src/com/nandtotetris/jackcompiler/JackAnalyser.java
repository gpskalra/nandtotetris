package com.nandtotetris.jackcompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Driver class for project 10.
 * Operates on a given source where source is
 * either a file name of the form xxx.jack or a
 * directory name containing one or more such files.
 * For each source xxx.jack file, the analyser goes
 * through the following logic:
 * 1. Create a JackTokenizer from the xxx.jack file.
 * 2. Create an output file called xxx.xml file and
 *    prepare it for writing.
 * 3. Use the CompilationEngine module to compile the
 *    input JackTokenizer into the xxx.xml file.
 *
 * @author gaganpreet1810@gmail.com
 */
public class JackAnalyser {

    /**
     * The input is a jack file. Opens a xxxT.xml file,
     * for xxx.jack input file and writes the tokens contained
     * in the xxx.jack file into the xxxT.xml file.
     *
     * The format for the xml xxxT.xml is as follows:
     * <tokens>TOKEN</tokens>
     * where TOKEN can be:
     * <keyword>KEYWORD</keyword>
     * <symbol>SYMBOL</symbol>
     * <integerConstant>INT_CONST</integerConstant>
     * <stringConstant>STRING_CONST</stringConstant>
     * <identifier>IDENTIFIER</identifier>
     * Four symbols are treated in a separate way:
     * < : &lt;
     * > : &gt;
     * " : &quot;
     * &: &amp;
     *
     * @param jackFile the input jack file
     */
    private static void tokenizeFile(File jackFile) {

        File outputFile = new File (jackFile.getAbsolutePath().replaceAll("\\.jack","T.xml"));
        // System.out.println("The output file name is: " + outputFile.getAbsolutePath());

        try {
            PrintWriter outputWriter = new PrintWriter(outputFile);

            outputWriter.println("<tokens>");

            JackTokenizer tokenizer = new JackTokenizer(jackFile);

            while (tokenizer.hasMoreTokens()) {

                tokenizer.advance();

                // System.out.println("Info: Current Token = " + tokenizer.getCurrentToken());
                // System.out.println("Info: Token Type = " + tokenizer.tokenType().toString());

                switch (tokenizer.tokenType()) {
                    case TOKEN_IDENTIFIER:
                        outputWriter.println("<identifier>" + tokenizer.identifier() + "</identifier>");
                        break;
                    case TOKEN_INT_CONST:
                        outputWriter.println("<integerConstant>" + Integer.toString(tokenizer.intVal()) + "</integerConstant>");
                        break;
                    case TOKEN_STRING_CONST:
                        outputWriter.println("<stringConstant>"+ tokenizer.stringVal() + "</stringConstant>");
                        break;
                    case TOKEN_KEYWORD:
                        outputWriter.println("<keyword>"+ tokenizer.keyword().getKeywordString() + "</keyword>");
                        break;
                    case TOKEN_SYMBOL:
                        outputWriter.print("<symbol>");
                        switch (tokenizer.symbol()) {
                            case '<' :
                                outputWriter.print("&lt;");
                                break;
                            case '>':
                                outputWriter.print("&gt;");
                                break;
                            case '&':
                                outputWriter.print("&amp;");
                                break;
                            default:
                                outputWriter.print(tokenizer.symbol());
                                break;
                        }
                        outputWriter.println("</symbol>");
                        break;
                    default:
                        break;
                }
            }
            outputWriter.println("</tokens>");
            outputWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * The input can be a directory or a jack file.
     * If the input is a xxx.jack file, opens a xxxT.xml
     * file, writes the tokens contained in the input file
     * into it.
     * If the input is a directory, opens a xxxT.xml file for
     * each jack file in the directory, and writes the tokens
     * contained in the xxx.jack file into it.
     *
     * @param input The input File object
     */
    private static void tokenize(File input) {

        if (input.isDirectory()) {
            // list of files to process
            File[] listOfInputFiles = input.listFiles();

            for (File file : listOfInputFiles) {
                if (isJackFile(file)) {
                    tokenizeFile(file);
                }
            }
        }

        else {
            tokenizeFile(input);
        }

    }

    /**
     * Driver for syntax analysis.
     *
     * For each input jack file, opens an
     * output xml file and then invokes the
     * compilation engine to write xml output
     * according to the jack analyser specification.
     *
     * @param input The input File object. Input can
     *              be a jack file or a directory with
     *              one or more jack files.
     */
    private static void analyseSyntax(File input) {

        if (input.isDirectory()) {
            File[] inputFiles = input.listFiles();
            for (File file:inputFiles) {
                if (Utils.isJackFile(file)) {
                    File outputFile = new File(file.getAbsolutePath().replaceAll(".jack",".xml"));
                    CompilationEngine engine = new CompilationEngine(file,outputFile);
                    engine.compileClass();
                }
            }
        }
        else {
            File outputFile = new File(input.getAbsolutePath().replaceAll(".jack",".xml"));
            CompilationEngine engine = new CompilationEngine(input,outputFile);
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

        // tokenize(input);
        analyseSyntax(input);

    }

}
