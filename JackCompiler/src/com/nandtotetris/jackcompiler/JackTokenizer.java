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

    private LineNumberReader inputReader;

    private String inFileName;

    /**
     * Constructs a new tokeniser object to tokenise
     * an input file.
     *
     * @param file
     */
    public JackTokenizer(File file) {

        try {
            inputReader = new LineNumberReader(new FileReader(file));
            currentToken = null;
            inFileName = file.getName();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if the input stream has more tokens.
     * If there are more tokens, moves the current file
     * pointer to the beginning of the next token.
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
                                /*if (currentChar == '\"') {
                                    // System.out.println("Info: hasMoreTokens: First character is a quote");
                                }*/
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
     * Returns true if an input character is a
     * number.
     *
     * @param inputChar the input character
     * @return true if 0 <= inputChar <= 9
     *         false otherwise
     */
    private Boolean isANumber(char inputChar) {
        return '0' <= inputChar && inputChar <= '9';
    }

    /**
     * Returns true if an input character is a
     * letter of the English alphabet
     * @param inputChar the input character
     * @return true if inputChar is a letter
     *         false otherwise
     */
    private Boolean isALetter(char inputChar) {
        return ('a' <= inputChar && inputChar <= 'z') || ('A' <= inputChar && inputChar <= 'Z');
     }

    /**
     * Returns the token type of the current token.
     *
     * @return TokenType of token string currentToken
     *         currentToken must be ensured to be
     *         non empty.
     */
    public TokenType tokenType() {

        char firstChar = currentToken.charAt(0);

        if (isANumber(firstChar)) {
            return TokenType.TOKEN_INT_CONST;
        }
        if (isALetter(firstChar)) {
            if (Keyword.get(currentToken) != null) {
                return TokenType.TOKEN_KEYWORD;
            }
            return TokenType.TOKEN_IDENTIFIER;
        }
        if (firstChar == '"') {
            return TokenType.TOKEN_STRING_CONST;
        }
        if (firstChar == '_') {
            return TokenType.TOKEN_IDENTIFIER;
        }

        return TokenType.TOKEN_SYMBOL;
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
    private Boolean isIdentifierChar(int inputChar) {
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
                if (!isIdentifierChar(currentChar)) {
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
            // The first character should be a quote
            int currentChar = inputReader.read();
            if (currentChar != '\"') {
                System.out.println("Error: JackTokenizer getStringConst The first character should be a quote");
                System.exit(1);
            }
            stringConst = stringConst + (char) currentChar;

            while (true) {
                currentChar = inputReader.read();
                stringConst = stringConst + (char) currentChar;
                if (currentChar == '\"') {
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
     *
     * Sets the currentToken to the next token in the input
     * stream. Should be called only when hasMoreTokens returns
     * true. This ensures that we currently point to a location
     * in the input stream where the next character marks the
     * beginning of some token. The advance method then
     * moves the file pointer to the end of the token.
     */
    public void advance() {

        try {
            inputReader.mark(1);
            int currentChar = inputReader.read();

            // identifier or keyword
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
                    if (currentChar == '\"') {
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

    /**
     * Returns the keyword corresponding to the currentToken.
     * Should be called only when the currentToken's TokenType
     * is keyword.
     * @return Keyword enum corresponding to the currentToken.
     */
    public Keyword keyword() {
        return Keyword.get(currentToken);
    }

    /**
     * returns the character which is the current token.
     * Should be called only when tokenType() is symbol.
     * @return the character which is currentToken
     */
    public char symbol() {
        return currentToken.charAt(0);
    }

    /**
     * returns the identifier which is the current token.
     * Should be called only when tokenType() is identifier.
     * @return the identifier which is currentToken
     */
    public String identifier() {
        return currentToken;
    }
    /**
     * returns the int const which is the current token.
     * Should be called only when tokenType() is int const.
     * @return the int const which is currentToken
     */
    public int intVal() {

        if (tokenType() != TokenType.TOKEN_INT_CONST) {
            System.out.println("Error: JackTokenizer intVal called when tokenType() is not INT_CONST");
            System.exit(1);
        }

        int intVal = 0;

        try {
            intVal = Integer.parseInt(currentToken);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return intVal;
    }

    /**
     * Returns the string constant without the quotes,
     * which is the current token.
     *
     * @return string_const if currentToken is "string_const"
     */
    public String stringVal() {
        String stringVal = currentToken.replaceAll("([\"])(.*)([\"])","$2");
        return stringVal;
    }

    /**
     * Returns the current line number in the
     * input stream.
     * @return line number of the currently
     *         marked position in the input stream.
     */
    public int lineNumber() {
        // getLineNumber starts line numbers from 0
        return inputReader.getLineNumber() + 1;
    }

    /**
     * returns the name of the input file.
     * @return name of the input file.
     */
    public String fileName() {
        return inFileName;
    }

}