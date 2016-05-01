package com.nandtotetris.jackcompiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * Whenever a compilexxx method is called,
 * the tokenizer's currentToken is set to the
 * first token that the compilexxx method has to
 * process.
 *
 * @author gaganpreet1810@gmail.com
 */
public class CompilationEngine {

    PrintWriter outputWriter;
    JackTokenizer tokenizer;
    private boolean lastSymbolReached;

    /**
     * Creates a new compilation engine with
     * the given input and output. The next routine
     * called must be compileClass since the constructor
     * calls advance() to make sure that tokenizer's
     * currentToken is the first token.
     *
     * @param inputFile File object for the
     *                  input jack file
     * @param outputFile The output xml file (TBD)
     */
    public CompilationEngine(File inputFile,File outputFile) {
        try {
            outputWriter = new PrintWriter(outputFile);
            tokenizer = new JackTokenizer(inputFile);
            lastSymbolReached = false;
            advance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compiles a complete class
     */
    public void compileClass() {

        outputWriter.println("<class>");

        // class
        compileKeyword(Keyword.CLASS);
        // className
        compileIdentifier();
        // {
        compileSymbol('{');
        // classVarDec* subroutineDec*
        while (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD &&
                (tokenizer.keyword()==Keyword.STATIC ||
                        tokenizer.keyword()==Keyword.FIELD)) {

            compileClassVarDec(tokenizer.keyword());
        }
        while (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD &&
                (tokenizer.keyword()==Keyword.CONSTRUCTOR ||
                        tokenizer.keyword()==Keyword.FUNCTION ||
                        tokenizer.keyword()==Keyword.METHOD)) {

            compileSubroutine(tokenizer.keyword());
        }

        // }
        lastSymbolReached = true;
        compileSymbol('}');

        outputWriter.println("</class>");
        outputWriter.close();
    }

    /**
     * Compiles a complete subroutine: method,
     * function or constructor.
     *
     * @param keyword keyword determining whether it
     *                is a method, function or
     *                constructor.
     */
    private void compileSubroutine(Keyword keyword) {

        // System.out.println("Info: Compiling Subroutine");
        outputWriter.println("<subroutineDec>");

        // method, function or constructor
        compileKeyword(keyword);

        // return type: (void | type)
        if (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD
                && tokenizer.keyword()==Keyword.VOID) {
            compileKeyword(Keyword.VOID);
        }
        else {
            compileType();
        }

        // subroutineName
        compileIdentifier();
        compileSymbol('(');
        compileParameterList();
        compileSymbol(')');
        compileSubroutineBody();

        outputWriter.println("</subroutineDec>");
    }

    /**
     * Compiles a subroutine body.
     */
    private void compileSubroutineBody() {

        outputWriter.println("<subroutineBody>");

        compileSymbol('{');
        while (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD
                && tokenizer.keyword()==Keyword.VAR) {
            compileVarDec();
        }
        compileStatements();
        compileSymbol('}');

        outputWriter.println("</subroutineBody>");
    }

    /**
     * Compiles a sequence of zero or more statements.
     */
    private void compileStatements() {

        outputWriter.println("<statements>");

        while (tokenizer.tokenType() == TokenType.TOKEN_KEYWORD) {

            switch (tokenizer.keyword()) {
                case LET:
                    compileLetStatement();
                    break;
                case IF:
                    compileIfStatement();
                    break;
                case WHILE:
                    compileWhileStatement();
                    break;
                case DO:
                    compileDoStatement();
                    break;
                case RETURN:
                    compileReturnStatement();
                    break;
                default:
                    System.out.println("Error: In file " + tokenizer.fileName()
                            + " line " + tokenizer.lineNumber()
                            + " Expected one of: let if while do return");
                    System.exit(1);
                    break;
            }
        }

        outputWriter.println("</statements>");
    }

    /**
     * Compiles a return statement
     */
    private void compileReturnStatement() {

        outputWriter.println("<returnStatement>");

        compileKeyword(Keyword.RETURN);

        if (!((tokenizer.tokenType() == TokenType.TOKEN_SYMBOL)
                && (tokenizer.symbol()==';'))) {
            compileExpression();
        }
        compileSymbol(';');

        outputWriter.println("</returnStatement>");
    }

    /**
     * Checks whether a given symbol is a
     * binary operator as per the jack grammar
     * @param symbol the input symbol
     * @return true if symbol is an operator
     *         false otherwise
     */
    private boolean isOp(char symbol) {
        final char[] opsArray = {'+','-','*','/','&','|','<','>','='};
        for (char s : opsArray) {
            if (symbol == s) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compiles a jack expression.
     */
    private void compileExpression() {

        outputWriter.println("<expression>");
        compileTerm();
        while (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && isOp(tokenizer.symbol())) {
            compileSymbol(tokenizer.symbol());
            compileTerm();
        }
        outputWriter.println("</expression>");
    }

    /**
     * Compiles a single term in an expression
     */
    private void compileTerm() {

        outputWriter.println("<term>");

        switch (tokenizer.tokenType()) {
            case TOKEN_INT_CONST:
                outputWriter.print("<integerConstant>");
                outputWriter.print(tokenizer.intVal());
                outputWriter.print("</integerConstant>\n");
                advance();
                break;
            case TOKEN_STRING_CONST:
                outputWriter.print("<stringConstant>");
                outputWriter.print(tokenizer.stringVal());
                outputWriter.print("</stringConstant>\n");
                advance();
                break;
            case TOKEN_KEYWORD:
                switch (tokenizer.keyword()) {
                    case TRUE:
                    case FALSE:
                    case NULL:
                    case THIS:
                        compileKeyword(tokenizer.keyword());
                        break;
                    default:
                        System.out.println("Error: In file " + tokenizer.fileName()
                                + " line " + tokenizer.lineNumber()
                                + " Expected one of: 'true' 'false' 'null' 'this'");
                        System.exit(1);
                        break;
                }
                break;
            case TOKEN_SYMBOL:
                switch (tokenizer.symbol()) {
                    case '(':
                        compileSymbol('(');
                        compileExpression();
                        compileSymbol(')');
                        break;
                    case '-':
                    case '~':
                        compileSymbol(tokenizer.symbol());
                        compileTerm();
                        break;
                    default:
                        System.out.println("Error: In file " + tokenizer.fileName()
                                + " line " + tokenizer.lineNumber()
                                + " Unexpected Symbol " + tokenizer.symbol());
                        System.exit(1);
                        break;

                }
                break;
            case TOKEN_IDENTIFIER:
                compileIdentifier();
                if (tokenizer.tokenType() == TokenType.TOKEN_SYMBOL) {
                    if (tokenizer.symbol() == '[') {
                        compileSymbol('[');
                        compileExpression();
                        compileSymbol(']');
                    }
                    if (tokenizer.symbol() == '(') {
                        compileSymbol('(');
                        compileExpressionList();
                        compileSymbol(')');
                    }
                    if (tokenizer.symbol() == '.') {
                        compileSymbol('.');
                        compileIdentifier();
                        compileSymbol('(');
                        compileExpressionList();
                        compileSymbol(')');
                    }
                }
                break;
        }

        outputWriter.println("</term>");

    }

    /**
     * Compiles a do statement.
     */
    private void compileDoStatement() {
        outputWriter.println("<doStatement>");
        compileKeyword(Keyword.DO);
        compileSubroutineCall();
        compileSymbol(';');
        outputWriter.println("</doStatement>");
    }

    /**
     * Compiles a subroutine call
     */
    private void compileSubroutineCall() {

        compileIdentifier();

        if (tokenizer.tokenType() != TokenType.TOKEN_SYMBOL) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " Expected Symbol '(' or '.'");
            System.exit(1);
        }

        switch (tokenizer.symbol()) {
            case '(':
                compileSymbol('(');
                compileExpressionList();
                compileSymbol(')');
                break;
            case '.':
                compileSymbol('.');
                compileIdentifier();
                compileSymbol('(');
                compileExpressionList();
                compileSymbol(')');
                break;
            default:
                System.out.println("Error: In file " + tokenizer.fileName()
                        + " line " + tokenizer.lineNumber()
                        + " Expected Symbol '(' or '.'");
                System.exit(1);
                break;
        }
    }

    /**
     * Compiles an expression list.
     * (expression(','expression)*)?
     */
    private void compileExpressionList() {
        outputWriter.println("<expressionList>");
        if (!(tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==')')) {
            compileExpression();
            while (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                    && tokenizer.symbol()==',') {
                compileSymbol(',');
                compileExpression();
            }
        }
        outputWriter.println("</expressionList>");
    }

    /**
     * Compiles a while Statement
     */
    private void compileWhileStatement() {

        outputWriter.println("<whileStatement>");

        compileKeyword(Keyword.WHILE);
        compileSymbol('(');
        compileExpression();
        compileSymbol(')');
        compileSymbol('{');
        compileStatements();
        compileSymbol('}');

        outputWriter.println("</whileStatement>");
    }

    /**
     * Compiles an if statement
     */
    private void compileIfStatement() {

        outputWriter.println("<ifStatement>");

        compileKeyword(Keyword.IF);
        compileSymbol('(');
        compileExpression();
        compileSymbol(')');
        compileSymbol('{');
        compileStatements();
        compileSymbol('}');

        if (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD
                && tokenizer.keyword()==Keyword.ELSE) {
            compileKeyword(Keyword.ELSE);
            compileSymbol('{');
            compileStatements();
            compileSymbol('}');
        }

        outputWriter.println("</ifStatement>");
    }

    /**
     * Compiles a let statement
     */
    private void compileLetStatement() {

        outputWriter.println("<letStatement>");

        compileKeyword(Keyword.LET);
        compileIdentifier();

        if (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL) {
            switch (tokenizer.symbol()) {
                case '=':
                    compileSymbol('=');
                    break;
                case '[':
                    compileSymbol('[');
                    compileExpression();
                    compileSymbol(']');
                    compileSymbol('=');
                    break;
                default:
                    System.out.println("Error: In file " + tokenizer.fileName()
                            + " line " + tokenizer.lineNumber()
                            + " Expected one of: '[' '=' ");
                    System.exit(1);
                    break;
            }
        }

        compileExpression();
        compileSymbol(';');

        outputWriter.println("</letStatement>");
    }

    /**
     * Compiles a variable declaration in a
     * subroutine.
     */
    private void compileVarDec() {

        outputWriter.println("<varDec>");

        compileKeyword(Keyword.VAR);

        compileType();
        compileIdentifier();

        while (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==',') {

            compileSymbol(',');
            compileIdentifier();
        }

        // ;
        compileSymbol(';');

        outputWriter.println("</varDec>");
    }

    /**
     * Compiles a parameter list.
     */
    private void compileParameterList() {

        outputWriter.println("<parameterList>");

        if (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==')') {
            outputWriter.println("</parameterList>");
            return;
        }

        compileType();
        compileIdentifier();

        while (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==',') {
            compileSymbol(',');
            compileType();
            compileIdentifier();
        }

        outputWriter.println("</parameterList>");

    }

    /**
     * Compiles a class variable declaration. The caller
     * (compileClass) sees the first token to decide it
     * is a class variable declaration. The input stream
     * is advanced over the first token.
     *
     * @param keyword Describes the type of variable.
     *                Possible values: static, field
     */
    private void compileClassVarDec(Keyword keyword) {

        // System.out.println("Info: Compiling Class Variable Declaration");

        outputWriter.println("<classVarDec>");

        // static | field
        compileKeyword(keyword);
        // type
        compileType();
        // varName
        compileIdentifier();
        // (,varName)*;
        while (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==',') {

            compileSymbol(',');
            compileIdentifier();
        }
        // ;
        compileSymbol(';');

        outputWriter.println("</classVarDec>");
    }

    private void compileType() {

        if (tokenizer.tokenType() == TokenType.TOKEN_IDENTIFIER) {
            compileIdentifier();
        }
        else {
            if (tokenizer.tokenType() == TokenType.TOKEN_KEYWORD) {
                if (!(tokenizer.keyword() == Keyword.INT ||
                        tokenizer.keyword() == Keyword.CHAR ||
                        tokenizer.keyword() == Keyword.BOOLEAN)) {
                    System.out.println("Error: In file " + tokenizer.fileName()
                            + " line " + tokenizer.lineNumber()
                            + " CompilationEngine.compileType Expected one of: int, "
                            + "char, boolean keyword");
                    System.exit(1);
                }
                else {
                    compileKeyword(tokenizer.keyword());
                }
            }
            else {
                System.out.println("Error: In file " + tokenizer.fileName()
                        + " line " + tokenizer.lineNumber()
                        + " CompilationEngine.compileType Expected 'type'");
                System.exit(1);
            }
        }
    }

    /**
     * Compiles a keyword. Checks that the
     * current token is a keyword. The caller
     * passes a keyword. Verifies that the current
     * keyword is the passed keyword.
     *
     * Writes the corresponding xml output.
     * @param keyword The keyword that the current
     *                keyword should match.
     */
    private void compileKeyword(Keyword keyword) {

        if (tokenizer.tokenType() != TokenType.TOKEN_KEYWORD) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " CompilationEngine.compileKeyword tokenType "
                    + tokenizer.tokenType().toString()
                    + " does not match the required tokenType keyword");
            System.exit(1);
        }

        if (tokenizer.keyword() != keyword) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " CompilationEngine.compileKeyword Required keyword " +
                    keyword + " does not match the current token keyword " +
                    tokenizer.keyword().getKeywordString());
            System.exit(1);
        }

        outputWriter.println("<keyword>" + tokenizer.keyword().getKeywordString() + "</keyword>");
        advance();
    }

    /**
     * Compiles an identifier. Checks that
     * the current token is an identifier.
     *
     * Writes xml output for an identifier.
     */
    private void compileIdentifier() {

        if (tokenizer.tokenType() != TokenType.TOKEN_IDENTIFIER) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " CompilationEngine.compileIdentifier Identifier expected");
            System.exit(1);
        }

        outputWriter.println("<identifier>" + tokenizer.identifier() + "</identifier>");
        advance();
    }

    /**
     * Compiles a symbol. Checks that the
     * current token is a symbol. The caller
     * passes a symbol. Verifies that the current
     * symbol is the passed symbol.
     *
     * Writes the corresponding xml output.
     * @param symbol The symbol that the current
     *                symbol should match.
     */
    private void compileSymbol(char symbol) {

        if (tokenizer.tokenType() != TokenType.TOKEN_SYMBOL) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " CompilationEngine.compileSymbol Symbol " + symbol + " expected");
            System.exit(1);
        }

        if (tokenizer.symbol() != symbol) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " CompilationEngine.compileSymbol symbol " +
                    tokenizer.symbol() + " does not match expected symbol " + symbol);
            System.exit(1);
        }

        if (tokenizer.symbol()=='<') {
            outputWriter.println("<symbol> &lt; </symbol>");
        }
        else {
            if (tokenizer.symbol()=='>') {
                outputWriter.println("<symbol> &gt; </symbol>");
            }
            else {
                if (tokenizer.symbol()=='&') {
                    outputWriter.println("<symbol> &amp; </symbol>");
                }
                else {
                    outputWriter.println("<symbol>" + tokenizer.symbol() + "</symbol>");
                }
            }
        }

        if (!lastSymbolReached) {
            advance();
        }
    }

    /**
     * Wrapper over tokenizer.advance()
     *
     * Should be called when the compilation
     * engine expects more tokens. Reports error
     * if there are no more tokens. Else calls
     * tokenizer.advance()
     */
    private void advance() {

        if (!tokenizer.hasMoreTokens()) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " CompilationEngine.advance more tokens expected");
            System.exit(1);
        }

        tokenizer.advance();
        // System.out.println("Current Token = " + tokenizer.getCurrentToken());
    }
}