package com.nandtotetris.jackcompiler;

import java.io.*;

/**
 * Removes all comments and white space from the
 * input stream and breaks it into Jack language tokens
 * as specified by the Jack grammar.
 *
 * @author gaganpreet1810@gmail.com
 */
public class JackTokenizer {

    // set to null by the constructor
    private String currentToken;

    private Reader inputReader;

    /**
     * Constructs a new tokeniser object to tokenise
     * an input file.
     *
     * @param file
     */
    public JackTokenizer(File file) {

        try {
            inputReader = new BufferedReader(new FileReader(file));
            currentToken = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * Returns true if the input stream has more tokens
     *
     * @return true if there are more tokens among the
     *              remaining contents of the file. A file
     *              pointer marks the current location in the
     *              file.
     *         false otherwise.
     */
    public Boolean hasMoreTokens() {

        // Finite Automata with states:
        // 0: Start
        // 1: \s*
        // 2: \s*/
        // 3: \s*/\*
        // 4: \s*/\*[^\*]*\*
        // 5: \s*//
        // 6: true
        // 7: false
        // 8: error

        int state = 0;

        try {

            int currentChar;

            while(true) {

                switch (state) {
                    case 0:
                        inputReader.mark(2);
                        currentChar = inputReader.read();
                        switch (currentChar) {
                            case -1:
                                return false;
                            case ' ':
                            case '\t':
                            case '\n': case '\r':
                                state = 1;
                                break;
                            case '/':
                                state = 2;
                                break;
                            default:
                                inputReader.reset();
                                return true;
                        }
                        break;
                    case 1:
                        inputReader.mark(2);
                        currentChar = inputReader.read();
                        switch (currentChar) {
                            case -1:
                                return false;
                            case ' ':
                            case '\t':
                            case '\n': case '\r':
                                break;
                            case '/':
                                state = 2;
                                break;
                            default:
                                inputReader.reset();
                                return true;
                        }
                        break;
                    case 2:
                        currentChar = inputReader.read();
                        switch (currentChar) {
                            case '*':
                                state = 3;
                                break;
                            case '/':
                                state = 5;
                                break;
                            default:
                                inputReader.reset();
                                return true;
                        }
                        break;
                    case 3:
                        currentChar = inputReader.read();
                        switch (currentChar) {
                            case '*':
                                state = 4;
                                break;
                            case '\0':
                                System.out.println("Error: hasMoreTokens: Comment did not end");
                                System.exit(1);
                                break;
                            default:
                        }
                        break;
                    case 4:
                        currentChar = inputReader.read();
                        switch (currentChar) {
                            case '*':
                                state = 4;
                                break;
                            case '\0':
                                System.out.println("Error: hasMoreTokens: Comment did not end");
                                System.exit(1);
                                break;
                            case '/':
                                state = 0;
                                break;
                            default:
                                state = 3;
                        }
                        break;
                    case 5:
                        currentChar = inputReader.read();
                        switch (currentChar) {
                            case '\n': case '\r':
                                state = 0;
                                break;
                            case -1:
                                return false;
                            default:
                        }
                        break;
                    default:
                        System.out.println("Error: hasMoreTokens: Should never enter default case");
                        System.exit(1);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Error: hasMoreTokens: Should not reach this return statement");
        return true;
    }

    /**
     * Returns the token type of the current token.
     *
     * @return TokenType of token stored in currentToken
     */
    public TokenType tokenType() {


        return null;
    }

    /**
     * Returns true if input char is a whitespace
     * @param inputChar the input character
     * @return true if inputChar is a whitespace
     *         false otherwise
     */
    private Boolean isWhiteSpace(char inputChar) {
        return inputChar == ' ' || inputChar == '\t' || inputChar == '\n' || inputChar == '\r';
    }

    /**
     * Returns true if an input character can be a
     * part of an identifer as specified by the jack
     * grammar.
     *
     * @param inputChar the input character
     * @return true if inputChar can be part of an identifier
     *         false otherwise
     */
    private Boolean isIdentiferChar(int inputChar) {
        return ('a' <= inputChar && inputChar <= 'z') ||
                ('A' <= inputChar && inputChar <= 'Z') ||
                ('0' <= inputChar && inputChar <= '9') ||
                inputChar == '_';
    }

    /**
     * Gets the identifier beginning at the current
     * position in the input stream.
     *
     * @return the next identifier in the stream
     */
    private String getIdentifier() {

        String identifier = "";

        try {

            while (true) {
                inputReader.mark(1);
                int currentChar = inputReader.read();
                if (!isIdentiferChar(currentChar)) {
                    inputReader.reset();
                    return identifier;
                }
                identifier = identifier + (char) currentChar;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return identifier;
    }

    /**
     * Gets an integer from the input stream.
     * The pointer in the input stream should be at
     * the beginning of the integer.
     * @return the integer as a string
     */
    private String getInt() {

        String integer = "";

        try {
            while (true) {
                inputReader.mark(1);
                int currentChar = inputReader.read();
                if (!('0' <= currentChar && currentChar <= '9')) {
                    inputReader.reset();
                    return integer;
                }
                integer = integer + (char) currentChar;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return integer;
    }

    /**
     * Get a string constant of the form <"string">
     * from the input stream. The pointer in the input
     * stream should be before the initial quote.
     * @return quoted string beginning at the pointer
     *         in the input file.
     */
    private String getStringConst() {

        String stringConst = "";

        try {
            // append a '"'
            int currentChar = inputReader.read();
            stringConst = stringConst + currentChar;

            while (true) {

                currentChar = inputReader.read();
                stringConst = stringConst + currentChar;
                if (currentChar == '"') {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringConst;
    }

    /**
     * Advances in the input stream.
     * Sets the currentToken to the next token in the input
     * stream. Should be called only when hasMoreTokens returns
     * true. This ensures that we currently point to a location
     * in the input stream where the next character marks the
     * beginning of some token.
     */
    public void advance() {

        try {
            inputReader.mark(1);
            int currentChar = inputReader.read();

            // identifer or keyword
            if (currentChar == '_' || ('a' <= currentChar && currentChar <= 'z')
                    || ('A' <= currentChar && currentChar <= 'Z')) {
                inputReader.reset();
                currentToken = getIdentifier();
            }
            else { // integer constant
                if ('0' <= currentChar && currentChar <= '9') {
                    inputReader.reset();
                    currentToken = getInt();
                }
                else { // string constant
                    if (currentChar == '"') {
                        inputReader.reset();
                        currentToken = getStringConst();
                    }
                    else { // symbol
                        currentToken = "" + (char) currentChar;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Getter for the member currentToken
     * @return currentToken member variable
     */
    public String getCurrentToken() {
        return currentToken;
    }

}
