package com.nandtotetris.jackcompiler;

import java.io.File;

/**
 * The main compilation engine. This class
 * performs a recursive top down parsing
 * and compilation. The first method to be
 * called after constructor should be
 * compileClass.
 *
 * @author gaganpreet1810@gmail.com
 */
public class CompilationEngine {

    VMWriter codeWriter;
    JackTokenizer tokenizer;
    SymbolTable symbolTable;

    /**
     * Creates a new compilation engine with
     * the given input and output. The next routine
     * called must be compileClass.
     *
     * @param inputFile File object for the
     *                  input jack file
     * @param outputVMFile The output vm file
     */
    public CompilationEngine(File inputFile,File outputVMFile) {
        codeWriter = new VMWriter(outputVMFile);
        tokenizer = new JackTokenizer(inputFile);
        symbolTable = new SymbolTable();
    }

    /**
     * Compiles a complete class
     */
    public void compileClass() {

        String className;

        advance();
        checkToken(TokenType.TOKEN_KEYWORD);
        checkKeyword(Keyword.CLASS);

        advance();
        checkToken(TokenType.TOKEN_IDENTIFIER);
        className = tokenizer.identifier();

        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('{');

        advance();
        while (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD &&
                (tokenizer.keyword()==Keyword.STATIC ||
                        tokenizer.keyword()==Keyword.FIELD)) {

            compileClassVarDec();
            advance();
        }
        while (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD &&
                (tokenizer.keyword()==Keyword.CONSTRUCTOR ||
                        tokenizer.keyword()==Keyword.FUNCTION ||
                        tokenizer.keyword()==Keyword.METHOD)) {

            compileSubroutine(className);
            advance();
        }

        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('}');
        codeWriter.close();
    }

    /**
     * Compiles a complete subroutine: method,
     * function or constructor.
     *
     * @param className className to be used to
     *                  write vm code.
     */
    private void compileSubroutine(String className) {

        String subroutineType;
        String subroutineName;
        boolean isReturnTypeVoid = false;

        subroutineType = tokenizer.keyword().getKeywordString();
        // start a new scope
        symbolTable.startSubroutine();

        // ignore return type, assuming error free code
        // return type: (void | type)
        advance();
        if (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD
                && tokenizer.keyword()==Keyword.VOID) {
            isReturnTypeVoid = true;
        }

        // subroutineName
        advance();
        checkToken(TokenType.TOKEN_IDENTIFIER);
        subroutineName = tokenizer.identifier();

        // (
        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('(');

        compileParameterList();

        // )
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol(')');

        compileSubroutineBody(className,subroutineName,subroutineType,isReturnTypeVoid);
    }

    /**
     * Compiles a subroutine body.
     */
    private void compileSubroutineBody(String className,String subroutineName,
                                       String subroutineType, boolean isReturnTypeVoid) {
        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('{');

        advance();
        while (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD
                && tokenizer.keyword()==Keyword.VAR) {
            compileVarDec();
            advance();
        }

        // write vm function
        int numLocals = symbolTable.varCount(SymbolKind.VAR);
        codeWriter.writeFunction(className+"."+subroutineName,numLocals);

        // constructor should allocate memory for the object and set this pointer.
        // method should should set this pointer.
        switch (subroutineType) {
            case "constructor":
                int numFields = symbolTable.varCount(SymbolKind.FIELD);
                codeWriter.writePush(Segment.CONST,numFields);
                codeWriter.writeCall("Memory.alloc",1);
                codeWriter.writePop(Segment.POINTER,0);
                break;
            case "method":
                codeWriter.writePush(Segment.ARG,0);
                codeWriter.writePop(Segment.POINTER,0);
                break;
            default:
                break;
        }

        // compileStatements();

        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('}');
    }

    /**
     * Compiles a sequence of zero or more statements.
     */
    private void compileStatements() {

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
                    break;
            }
            advance();
        }
    }

    /**
     * Compiles a return statement
     */
    private void compileReturnStatement() {

        compileKeyword(Keyword.RETURN);

        if (!((tokenizer.tokenType() == TokenType.TOKEN_SYMBOL)
                && (tokenizer.symbol()==';'))) {
            compileExpression();
        }
        compileSymbol(';');
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
        compileTerm();
        while (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && isOp(tokenizer.symbol())) {
            compileSymbol(tokenizer.symbol());
            compileTerm();
        }
    }

    /**
     * Compiles a single term in an expression
     */
    private void compileTerm() {

        switch (tokenizer.tokenType()) {
            case TOKEN_INT_CONST:
                advance();
                break;
            case TOKEN_STRING_CONST:
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
    }

    /**
     * Compiles a do statement.
     */
    private void compileDoStatement() {
        compileKeyword(Keyword.DO);
        compileSubroutineCall();
        compileSymbol(';');
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
        if (!(tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==')')) {
            compileExpression();
            while (tokenizer.tokenType() == TokenType.TOKEN_SYMBOL
                    && tokenizer.symbol() == ',') {
                compileSymbol(',');
                compileExpression();
            }
        }
    }

    /**
     * Compiles a while Statement
     */
    private void compileWhileStatement() {

        compileKeyword(Keyword.WHILE);
        compileSymbol('(');
        compileExpression();
        compileSymbol(')');
        compileSymbol('{');
        compileStatements();
        compileSymbol('}');

    }

    /**
     * Compiles an if statement
     */
    private void compileIfStatement() {

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

    }

    /**
     * Compiles a let statement
     */
    private void compileLetStatement() {

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
    }

    /**
     * Compiles a variable declaration in a
     * subroutine.
     */
    private void compileVarDec() {

        String symbolType = null;
        String symbolName = null;

        advance();
        symbolType = compileType();

        advance();
        checkToken(TokenType.TOKEN_IDENTIFIER);
        symbolName = tokenizer.identifier();
        symbolTable.define(symbolName,symbolType,SymbolKind.VAR);

        advance();
        while (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==',') {
            advance();
            checkToken(TokenType.TOKEN_IDENTIFIER);
            symbolName = tokenizer.identifier();
            symbolTable.define(symbolName,symbolType,SymbolKind.VAR);
            advance();
        }

        // ;
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol(';');
    }

    /**
     * Compiles a parameter list.
     */
    private void compileParameterList() {

        String symbolType = null;
        String symbolName = null;

        advance();
        if (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==')') {
            return;
        }

        symbolType = compileType();
        advance();
        checkToken(TokenType.TOKEN_IDENTIFIER);
        symbolName = tokenizer.identifier();
        symbolTable.define(symbolName,symbolType,SymbolKind.ARG);
        advance();
        while (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==',') {
            advance();
            symbolType = compileType();
            advance();
            checkToken(TokenType.TOKEN_IDENTIFIER);
            symbolName = compileIdentifier();
            symbolTable.define(symbolName,symbolType,SymbolKind.ARG);
            advance();
        }
    }

    /**
     * Compiles a class variable declaration. The caller
     * (compileClass) sees the first token to decide it
     * is a class variable declaration. The currentToken
     * is the first token of the declaration.
     */
    private void compileClassVarDec() {

        Keyword symbolKindKeyword;
        String symbolType;
        String symbolName;
        SymbolKind symbolKind;

        // static | field
        symbolKindKeyword = tokenizer.keyword();

        // type
        advance();
        symbolType = compileType();

        // varName
        advance();
        checkToken(TokenType.TOKEN_IDENTIFIER);
        symbolName = tokenizer.identifier();

        // add to symbol table
        symbolKind = (symbolKindKeyword == Keyword.STATIC)
                ? SymbolKind.STATIC : SymbolKind.FIELD;
        symbolTable.define(symbolName,symbolType,symbolKind);

        advance();
        // (,varName)*;
        while (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==',') {

            // varName
            advance();
            checkToken(TokenType.TOKEN_IDENTIFIER);
            symbolName = tokenizer.identifier();

            // add to symbol table
            symbolTable.define(symbolName,symbolType,symbolKind);

            advance();
        }

        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol(';');
    }

    /**
     * Verifies that the current token's
     * token type matches the input token
     * type. If it does not, report an error.
     * @param tokenType the input TokenType
     */
    private void checkToken(TokenType tokenType) {
        if (tokenizer.tokenType() != tokenType) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " current token type " + tokenizer.tokenType()
                    + " expected token type " + tokenType.toString());
        }
    }

    /**
     * Checks whether the current token
     * (which must be a symbol)
     * matches the input symbol.
     * @param symbol The input symbol
     */
    private void checkSymbol(char symbol) {

        if (tokenizer.symbol() != symbol) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " symbol " + tokenizer.symbol()
                    + " does not match expected symbol "
                    + symbol);
        }

    }

    /**
     * Checks whether the current token
     * (which must be a keyword)
     * matches the input keyword.
     * @param keyword The input keyword
     */
    private void checkKeyword(Keyword keyword) {
        if (tokenizer.keyword() != keyword) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " keyword " + tokenizer.keyword()
                    + " does not match expected keyword "
                    + keyword);
        }
    }

    /**
     * Compiles a type. A type is either a primitive
     * type: int, char, boolean or an identifier.
     *
     * @return The String representation of the type
     */
    private String compileType() {

        String type = null;
        if (tokenizer.tokenType() == TokenType.TOKEN_IDENTIFIER) {
            type = tokenizer.identifier();
        }
        else {
            if (tokenizer.tokenType() == TokenType.TOKEN_KEYWORD) {
                if (!(tokenizer.keyword() == Keyword.INT ||
                        tokenizer.keyword() == Keyword.CHAR ||
                        tokenizer.keyword() == Keyword.BOOLEAN)) {
                    System.out.println("Error: In file " + tokenizer.fileName()
                            + " line " + tokenizer.lineNumber()
                            + " Expected one of: int, "
                            + "char, boolean keyword");
                }
                else {
                    type = tokenizer.keyword().getKeywordString();
                }
            }
            else {
                System.out.println("Error: In file " + tokenizer.fileName()
                        + " line " + tokenizer.lineNumber()
                        + " Expected 'type'");
            }
        }
        return type;
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

        advance();
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
    }

    /**
     * Compiles an identifier.
     * @return the identifier string
     */
    private String compileIdentifier() {
        return tokenizer.identifier();
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

        advance();

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

        switch (tokenizer.symbol()) {
            case '<':

                break;
            case '>':
                break;
            case '&':
                break;
            default:
                break;
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
                    + " more tokens expected");
            return;
        }
        tokenizer.advance();
    }
}