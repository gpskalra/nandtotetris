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
    String className;
    String currentSubroutine;
    String returnType;
    String currentSubroutineType;
    int labelCount;
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
        labelCount = 0;
    }

    /**
     * Compiles a complete class
     */
    public void compileClass() {

        // String className;

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

            compileSubroutine();
            advance();
        }

        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('}');
        codeWriter.close();
    }

    /**
     * Compiles a complete subroutine: method,
     * function or constructor.
     */
    private void compileSubroutine() {


        currentSubroutineType = tokenizer.keyword().getKeywordString();
        // start a new scope
        symbolTable.startSubroutine();
        if (currentSubroutineType == "method") {
            symbolTable.define("this",className,SymbolKind.ARG);
        }

        // ignore return type, assuming error free code
        // return type: (void | type)
        advance();
        returnType = compileReturnType();

        // subroutineName
        advance();
        checkToken(TokenType.TOKEN_IDENTIFIER);
        currentSubroutine = tokenizer.identifier();

        // (
        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('(');

        compileParameterList();

        // )
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol(')');

        compileSubroutineBody();
    }

    private String compileReturnType() {

        String type = null;
        if (tokenizer.tokenType() == TokenType.TOKEN_IDENTIFIER) {
            type = tokenizer.identifier();
        }
        else {
            if (tokenizer.tokenType() == TokenType.TOKEN_KEYWORD) {
                if (!(tokenizer.keyword() == Keyword.INT ||
                        tokenizer.keyword() == Keyword.CHAR ||
                        tokenizer.keyword() == Keyword.BOOLEAN ||
                tokenizer.keyword() == Keyword.VOID)) {
                    System.out.println("Error: In file " + tokenizer.fileName()
                            + " line " + tokenizer.lineNumber()
                            + " Expected one of: int, "
                            + "char, boolean, void keyword");
                }
                else {
                    type = tokenizer.keyword().getKeywordString();
                }
            }
            else {
                System.out.println("Error: In file " + tokenizer.fileName()
                        + " line " + tokenizer.lineNumber()
                        + " Expected 'return type'");
            }
        }
        return type;
    }

    /**
     * Compiles a subroutine body.
     */
    private void compileSubroutineBody() {

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
        codeWriter.writeFunction(className + "." + currentSubroutine,numLocals);

        // constructor should allocate memory for the object and set this pointer.
        // method should should set this pointer.
        switch (currentSubroutineType) {
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

        compileStatements();
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
                    advance();
                    break;
                case IF:
                    compileIfStatement();
                    break;
                case WHILE:
                    compileWhileStatement();
                    advance();
                    break;
                case DO:
                    compileDoStatement();
                    advance();
                    break;
                case RETURN:
                    compileReturnStatement();
                    advance();
                    break;
                default:
                    System.out.println("Error: In file " + tokenizer.fileName()
                            + " line " + tokenizer.lineNumber()
                            + " Expected one of: let if while do return");
                    break;
            }
        }
    }

    /**
     * Compiles a return statement
     */
    private void compileReturnStatement() {

        advance();

        if (returnType == "void") {
            checkToken(TokenType.TOKEN_SYMBOL);
            checkSymbol(';');
            codeWriter.writePush(Segment.CONST,0);
            codeWriter.writeReturn();
            return;

        }

        compileExpression();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol(';');
        codeWriter.writeReturn();
    }

    /**
     * Checks whether a given symbol is a
     * binary operator as per the jack grammar
     * @param symbol the input symbol
     * @return true if symbol is a binary operator
     *         false otherwise
     */
    private boolean isBinaryOp(char symbol) {
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

        if (tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && isBinaryOp(tokenizer.symbol())) {
            char op = tokenizer.symbol();
            advance();
            compileExpression();
            switch (op) {
                case '*':
                    codeWriter.writeCall("Math.multiply",2);
                    break;
                case '/':
                    codeWriter.writeCall("Math.divide",2);
                    break;
                default:
                    codeWriter.writeArithmetic(ArithmeticCommand.getCommandFromBinaryOp(op));
                    break;
            }
        }
    }

    /**
     * Compiles a single term in an expression
     */
    private void compileTerm() {

        switch (tokenizer.tokenType()) {
            case TOKEN_INT_CONST:
                codeWriter.writePush(Segment.CONST,tokenizer.intVal());
                advance();
                break;
            case TOKEN_STRING_CONST:
                int stringLength = tokenizer.stringVal().length();
                codeWriter.writePush(Segment.CONST,stringLength);
                codeWriter.writeCall("String.new",1);
                for(int i=0;i<stringLength;i++) {
                    codeWriter.writePush(Segment.CONST,(int) tokenizer.stringVal().charAt(i));
                    codeWriter.writeCall("String.appendChar",2);
                }
                advance();
                break;
            case TOKEN_KEYWORD:
                switch (tokenizer.keyword()) {
                    case TRUE:
                        codeWriter.writePush(Segment.CONST,1);
                        codeWriter.writeArithmetic(ArithmeticCommand.NEG);
                        break;
                    case FALSE:
                        codeWriter.writePush(Segment.CONST,0);
                        break;
                    case NULL:
                        codeWriter.writePush(Segment.CONST,0);
                    case THIS:
                        codeWriter.writePush(Segment.POINTER,0);
                        break;
                    default:
                        System.out.println("Error: In file " + tokenizer.fileName()
                                + " line " + tokenizer.lineNumber()
                                + " Expected one of: 'true' 'false' 'null' 'this'");
                        break;
                }
                advance();
                break;
            case TOKEN_SYMBOL:
                switch (tokenizer.symbol()) {
                    case '(':
                        advance();
                        compileExpression();
                        checkToken(TokenType.TOKEN_SYMBOL);
                        checkSymbol(')');
                        advance();
                        break;
                    case '-':
                    case '~':
                        char unaryOp = tokenizer.symbol();
                        advance();
                        compileTerm();
                        codeWriter.writeArithmetic(ArithmeticCommand.getCommandFromUnaryOp(unaryOp));
                        break;
                    default:
                        System.out.println("Error: In file " + tokenizer.fileName()
                                + " line " + tokenizer.lineNumber()
                                + " Unexpected Symbol " + tokenizer.symbol());
                        break;
                }
                break;
            case TOKEN_IDENTIFIER:
                int numArgs;
                String symbolName1 = tokenizer.identifier();
                String subroutineName;
                SymbolKind symbolKind1;
                advance();
                // looks like this if loop would always be executed
                // an identifier always seems to be followed by a symbol
                if (tokenizer.tokenType() == TokenType.TOKEN_SYMBOL) {
                    switch (tokenizer.symbol()) {
                        case '[':
                            advance();
                            compileExpression();
                            checkToken(TokenType.TOKEN_SYMBOL);
                            checkSymbol(']');
                            symbolKind1 = symbolTable.kindOf(symbolName1);
                            int index = symbolTable.indexOf(symbolName1);
                            codeWriter.writePush(SymbolKind.getSegment(symbolKind1),index);
                            codeWriter.writeArithmetic(ArithmeticCommand.ADD);
                            codeWriter.writePop(Segment.POINTER,1);
                            codeWriter.writePush(Segment.THAT,0);
                            advance();
                            break;
                        case '(':
                            advance();
                            codeWriter.writePush(Segment.POINTER,0);
                            numArgs = compileExpressionList();
                            checkToken(TokenType.TOKEN_SYMBOL);
                            checkSymbol(')');
                            codeWriter.writeCall(className+"."+symbolName1,numArgs+1);
                            advance();
                            break;
                        case '.':
                            advance();
                            checkToken(TokenType.TOKEN_IDENTIFIER);
                            String symbolName2 = tokenizer.identifier();
                            advance();
                            checkToken(TokenType.TOKEN_SYMBOL);
                            checkSymbol('(');
                            advance();
                            symbolKind1 = symbolTable.kindOf(symbolName1);
                            boolean isMethodCall = (symbolKind1 != SymbolKind.NONE);
                            if (isMethodCall) {
                                // the subroutine is a method, hence an
                                // extra argument needs to be passed
                                Segment memorySegment = SymbolKind.getSegment(symbolKind1);
                                codeWriter.writePush(memorySegment,symbolTable.indexOf(symbolName1));
                            }
                            // pushes the arguments to the stack
                            numArgs = compileExpressionList();
                            checkToken(TokenType.TOKEN_SYMBOL);
                            checkSymbol(')');
                            if (isMethodCall) {
                                subroutineName = symbolTable.typeOf(symbolName1)+"."+symbolName2;
                                codeWriter.writeCall(subroutineName,numArgs+1);
                            }
                            else {
                                subroutineName = symbolName1+"."+symbolName2;
                                codeWriter.writeCall(subroutineName,numArgs);
                            }
                            advance();
                            break;
                        default:
                            symbolKind1 = symbolTable.kindOf(symbolName1);
                            index = symbolTable.indexOf(symbolName1);
                            codeWriter.writePush(SymbolKind.getSegment(symbolKind1),index);
                            break;
                    }
                }
                break;
        }
    }

    /**
     * Compiles a do statement.
     */
    private void compileDoStatement() {
        advance();
        compileSubroutineCall();
        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol(';');
        // ignore the return value which is the constant 0
        codeWriter.writePop(Segment.TEMP,0);
    }

    /**
     * Compiles a subroutine call
     */
    private void compileSubroutineCall() {

        checkToken(TokenType.TOKEN_IDENTIFIER);
        String symbolName1 = tokenizer.identifier();

        advance();

        if (tokenizer.tokenType() != TokenType.TOKEN_SYMBOL) {
            System.out.println("Error: In file " + tokenizer.fileName()
                    + " line " + tokenizer.lineNumber()
                    + " Expected Symbol '(' or '.'");
        }

        int numArgs;
        String subroutineName;
        switch (tokenizer.symbol()) {
            case '(':
                advance();
                codeWriter.writePush(Segment.POINTER,0);
                numArgs = compileExpressionList();
                checkToken(TokenType.TOKEN_SYMBOL);
                checkSymbol(')');
                subroutineName = className+"."+symbolName1;
                codeWriter.writeCall(subroutineName,numArgs+1);
                break;
            case '.':
                advance();
                checkToken(TokenType.TOKEN_IDENTIFIER);
                String symbolName2 = tokenizer.identifier();
                advance();
                checkToken(TokenType.TOKEN_SYMBOL);
                checkSymbol('(');
                advance();
                SymbolKind symbolKind1 = symbolTable.kindOf(symbolName1);
                boolean isMethodCall = (symbolKind1 != SymbolKind.NONE);
                if (isMethodCall) {
                    // the subroutine is a method, hence an
                    // extra argument needs to be passed
                    Segment memorySegment = SymbolKind.getSegment(symbolKind1);
                    codeWriter.writePush(memorySegment,symbolTable.indexOf(symbolName1));
                }
                // pushes the arguments to the stack
                numArgs = compileExpressionList();
                checkToken(TokenType.TOKEN_SYMBOL);
                checkSymbol(')');
                if (isMethodCall) {
                    subroutineName = symbolTable.typeOf(symbolName1)+"."+symbolName2;
                    codeWriter.writeCall(subroutineName,numArgs+1);
                }
                else {
                    subroutineName = symbolName1+"."+symbolName2;
                    codeWriter.writeCall(subroutineName,numArgs);
                }
                break;
            default:
                System.out.println("Error: In file " + tokenizer.fileName()
                        + " line " + tokenizer.lineNumber()
                        + " Expected Symbol '(' or '.'");
                break;
        }
    }

    /**
     * Compiles an expression list.
     * (expression(','expression)*)?
     * Writes vm code to put result value of all
     * expressions on the stack.
     * @return number of expressions encountered
     */
    private int compileExpressionList() {
        int numExpressions = 0;
        if (!(tokenizer.tokenType()==TokenType.TOKEN_SYMBOL
                && tokenizer.symbol()==')')) {
            compileExpression();
            numExpressions += 1;
            while (tokenizer.tokenType() == TokenType.TOKEN_SYMBOL
                    && tokenizer.symbol() == ',') {
                advance();
                compileExpression();
                numExpressions += 1;
            }
        }
        return numExpressions;
    }

    /**
     * Compiles a while Statement
     */
    private void compileWhileStatement() {

        int myLabelCount = labelCount;
        labelCount = labelCount + 1;
        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('(');
        advance();
        codeWriter.writeLabel("WHILE"+myLabelCount);
        compileExpression();
        codeWriter.writePush(Segment.CONST,0);
        codeWriter.writeArithmetic(ArithmeticCommand.EQ);
        codeWriter.writeIf("IF_FALSE"+myLabelCount);
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol(')');
        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('{');
        advance();
        compileStatements();
        codeWriter.writeGoto("WHILE"+myLabelCount);
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('}');
        codeWriter.writeLabel("IF_FALSE"+myLabelCount);
    }

    /**
     * Compiles an if statement
     */
    private void compileIfStatement() {

        int myLabelCount = labelCount;
        labelCount = labelCount + 1;
        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('(');
        advance();
        compileExpression();
        // if condition expression is at the top of stack
        codeWriter.writePush(Segment.CONST,0);
        codeWriter.writeArithmetic(ArithmeticCommand.EQ);
        codeWriter.writeIf("IF_FALSE"+myLabelCount);
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol(')');
        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('{');
        advance();
        compileStatements();
        codeWriter.writeGoto("IF_END"+myLabelCount);
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol('}');
        advance();
        codeWriter.writeLabel("IF_FALSE"+myLabelCount);
        if (tokenizer.tokenType()==TokenType.TOKEN_KEYWORD
                && tokenizer.keyword()==Keyword.ELSE) {
            advance();
            checkToken(TokenType.TOKEN_SYMBOL);
            checkSymbol('{');
            advance();
            compileStatements();
            checkToken(TokenType.TOKEN_SYMBOL);
            checkSymbol('}');
            advance();
        }
        codeWriter.writeLabel("IF_END"+myLabelCount);
    }

    /**
     * Compiles a let statement
     */
    private void compileLetStatement() {

        String symbolName;
        Segment memorySegment;
        advance();
        checkToken(TokenType.TOKEN_IDENTIFIER);
        symbolName = tokenizer.identifier();
        memorySegment = SymbolKind.getSegment(symbolTable.kindOf(symbolName));
        advance();
        checkToken(TokenType.TOKEN_SYMBOL);
        boolean isArrayEntry = false;
        switch (tokenizer.symbol()) {
            case '=':
                advance();
                break;
            case '[':
                isArrayEntry = true;
                advance();
                compileExpression();
                checkToken(TokenType.TOKEN_SYMBOL);
                checkSymbol(']');
                advance();
                checkToken(TokenType.TOKEN_SYMBOL);
                checkSymbol('=');
                advance();
                break;
            default:
                System.out.println("Error: In file " + tokenizer.fileName()
                        + " line " + tokenizer.lineNumber()
                        + " Expected one of: '[' '=' ");
                break;
        }

        compileExpression();
        checkToken(TokenType.TOKEN_SYMBOL);
        checkSymbol(';');

        // In case of an array entry, the stack top has the
        // expression. The second to top entry is the index
        // into the array.
        if (isArrayEntry) {
            // pop rvalue
            codeWriter.writePop(Segment.TEMP,0);
            // set "that" segment
            codeWriter.writePush(memorySegment,symbolTable.indexOf(symbolName));
            codeWriter.writeArithmetic(ArithmeticCommand.ADD);
            codeWriter.writePop(Segment.POINTER,1);
            // use pop that 0 to do the final assignment
            codeWriter.writePush(Segment.TEMP,0);
            codeWriter.writePop(Segment.THAT,0);
        }
        else {
            codeWriter.writePop(memorySegment,symbolTable.indexOf(symbolName));
        }
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