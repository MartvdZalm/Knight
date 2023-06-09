package src.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import src.ast.*;
import src.lexer.*;

/**
 * A class responsible for parsing a source file into an Abstract Syntax Tree (AST).
 */
public class Parser
{
    private Lexer lexer;
    private Token token;
    private static final Token SENTINEL = new Token(Symbol.symbol("SENTINEL", Tokens.SENTINEL), 0, 0);
    private Deque<Token> stOperator = new ArrayDeque<>();
    private Deque<Expression> stOperand = new ArrayDeque<>();
	private Token currentAccessModifier;

	/**
     * Constructs a Parser object and initializes the lexer.
     *
     * @param sourceFile The path to the source file to be parsed.
     */
    public Parser(String sourceFile)
    {
        lexer = new Lexer(sourceFile);
        stOperator.push(SENTINEL);
    }

	/**
     * Parses the source file and constructs the Abstract Syntax Tree (AST).
     *
     * @return The root of the AST representing the parsed program.
     * @throws ParseException If there's an error during the parsing process.
     */
    public Tree parse() throws ParseException
    {
        Program program = null;

        try {
			List<ClassDecl> classList = new ArrayList<>();
			List<Include> includeList = new ArrayList<>();

			token = lexer.nextToken();
			currentAccessModifier = token;
            do {

                switch (token.getToken()) {
					case INCLUDE: {
						while (token != null && token.getToken() == Tokens.INCLUDE) {
							eat(Tokens.INCLUDE);
							includeList.add(new Include(token, new IdentifierExpr(token, token.getSymbol())));
							eat(Tokens.IDENTIFIER);
						}
					}	
					break;

					case CLASS: {
						while (token != null) {
							classList.add(parseClassDecl());
						}
					} 
					break;
    
                	default:
                    	throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
                }
                
            } while (token != null);
			
            program = new Program(currentAccessModifier, classList, includeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return program;
    }

	/**
     * Parses a class declaration in the source file.
     *
     * @return The parsed ClassDecl object representing the class declaration.
     * @throws ParseException If there's an error during the parsing process.
     */
	private ClassDecl parseClassDecl() throws ParseException
	{
		eat(Tokens.CLASS);
		IdentifierExpr className = new IdentifierExpr(token, token.getSymbol());
		eat(Tokens.IDENTIFIER);

		IdentifierExpr parent = null;
		if (token.getToken() == Tokens.LESSTHAN) {
			eat(Tokens.LESSTHAN);
			parent = new IdentifierExpr(token, token.getSymbol());
			eat(Tokens.IDENTIFIER);
		}
		eat(Tokens.LEFTBRACE);

		List<Declaration> varList = new ArrayList<>();

		while (token.getToken() != Tokens.RIGHTBRACE) {
			parseAccess();
			varList.add(parseVariable());
		}
		eat(Tokens.RIGHTBRACE);

		if (parent == null) {
			return new ClassDeclSimple(className.getToken(), className, varList);
		} else {
			return new ClassDeclExtends(className.getToken(), className, parent, varList);
		}
	}

	/**
     * Parses a variable declaration in the source file.
     *
     * @return The parsed Declaration object representing the variable declaration.
     * @throws ParseException If there's an error during the parsing process.
     */
	private Declaration parseVariable() throws ParseException
	{
		Declaration decl = null;
		Type type = parseType();
		Identifier id = new Identifier(token, token.getSymbol());

		eat(Tokens.IDENTIFIER);
		if (token.getToken() == Tokens.SEMICOLON) {
			decl = new VarDecl(token, type, id, currentAccessModifier);
			eat(Tokens.SEMICOLON);
		} else {
			eat(Tokens.ASSIGN);
			decl = new VarDeclInit(token, type, id, parseExpression(), currentAccessModifier);
		}

		if (token.getToken() == Tokens.SEMICOLON) {
			eat(Tokens.SEMICOLON);
		}

		return decl;
	}

	/**
     * Parses an expression in the source file.
     *
     * @return The parsed Expression object representing the expression.
     * @throws ParseException If there's an error during the parsing process.
     */
	private Expression parseExpression() throws ParseException
    {
        try {
            parseExpr();
            Token tok = stOperator.peek();
			while (tok.getToken() != Tokens.SENTINEL) {
				popOperator();
				tok = stOperator.peek();
			}
			return stOperand.pop();

        } catch (ParseException pe) {
            throw pe;
        } catch (Exception e) {
            System.err.println("Parser Error " + token.getRow() + ":" + token.getCol());
            throw e;
        }
    }

	/**
     * Parses a function body in the source file.
     *
     * @return The parsed Expression object representing the function body.
     * @throws ParseException If there's an error during the parsing process.
     */
	private Expression parseFunctionBody() throws ParseException
	{
		eat(Tokens.LEFTPAREN);
		List<ArgDecl> argList = new ArrayList<ArgDecl>();

		if (token.getToken() != Tokens.RIGHTPAREN) {
			argList.add(parseArgument());

			while (token.getToken() == Tokens.COMMA) {
				eat(Tokens.COMMA);;
				argList.add(parseArgument());
			}
		}
		eat(Tokens.RIGHTPAREN);
		eat(Tokens.LEFTBRACE);

		List<Declaration> varList = new ArrayList<>();
		List<Statement> statList = new ArrayList<>();

		while (token.getToken() != Tokens.RIGHTBRACE && token.getToken() != Tokens.RETURN) {

			if (token.getToken() == Tokens.INTEGER || token.getToken() == Tokens.STRING || token.getToken() == Tokens.IDENTIFIER) {	
				
				while (token.getToken() == Tokens.INTEGER || token.getToken() == Tokens.STRING) {
					varList.add(parseVariable());
				}
	
				while (token.getToken() == Tokens.IDENTIFIER) {
					IdentifierType identifierType = new IdentifierType(token, token.getSymbol());
					Identifier identifier = new Identifier(token, token.getSymbol());
					eat(Tokens.IDENTIFIER);
	
					if (token.getToken() == Tokens.IDENTIFIER) {
						Identifier identifier2 = new Identifier(token, token.getSymbol());
						eat(Tokens.IDENTIFIER);
						if (token.getToken() == Tokens.SEMICOLON) {
							varList.add(new VarDecl(token, identifierType, identifier2, currentAccessModifier));
						} else {
							varList.add(new VarDeclInit(token, identifierType, identifier2, parseExpression(), currentAccessModifier));
						}
						eat(Tokens.SEMICOLON);
					} else {		
						Statement stat = parseState1(identifier);
						statList.add(stat);
					}
				}
			} else {
				statList.add(parseStatement());
			}
		}

		Expression returnExpr = null;
		if (token.getToken() == Tokens.RETURN) {
			eat(Tokens.RETURN);
			returnExpr = parseExpression();
			eat(Tokens.SEMICOLON);
		} 
		eat(Tokens.RIGHTBRACE);

		if (returnExpr != null) {
			return new FunctionExprReturn(token, argList, varList, statList, returnExpr);
		} else {
			return new FunctionExprVoid(token, argList, varList, statList);
		}
	}

	/**
     * Parses a function call in the source file.
     *
     * @param methodId The identifier representing the method being called.
     * @return The parsed Expression object representing the function call.
     * @throws ParseException If there's an error during the parsing process.
     */
	private Expression parseCallFunction(IdentifierExpr methodId) throws ParseException
	{
		Token tok = token;
		List<Expression> exprList = new ArrayList<Expression>();

		eat(Tokens.LEFTPAREN);
		if (token.getToken() != Tokens.RIGHTPAREN) {
			Expression exprArg = parseExpression();
			exprList.add(exprArg);
			while (token.getToken() == Tokens.COMMA) {
				eat(Tokens.COMMA);
				exprArg = parseExpression();
				exprList.add(exprArg);
			}
		}
		eat(Tokens.RIGHTPAREN);
		return new CallFunctionExpr(tok, null, methodId, exprList);
	}

	/**
     * Parses an argument declaration in the source file.
     *
     * @return The parsed ArgDecl object representing the argument declaration.
     * @throws ParseException If there's an error during the parsing process.
     */
	private ArgDecl parseArgument() throws ParseException
	{
		Type argType = parseType();
		Identifier argId = null;
		if (token.getToken() == Tokens.IDENTIFIER) {
			argId = new Identifier(token, token.getSymbol());
		}
		eat(Tokens.IDENTIFIER);

		return new ArgDecl(argId.getToken(), argType, argId);
	}

	/**
	 * Parses a statement and returns the corresponding Statement object.
	 * 
	 * @return The parsed Statement object.
	 * @throws ParseException If there is an error while parsing the statement.
	 */
	private Statement parseStatement() throws ParseException
    {
		switch (token.getToken()) {

			case LEFTBRACE: {
				Token tok = token;
				eat(Tokens.LEFTBRACE);
				List<Statement> body = new ArrayList<>();
				while (token.getToken() != Tokens.RIGHTBRACE) {
					Statement stat = parseStatement();
					body.add(stat);
				}
				eat(Tokens.RIGHTBRACE);
				Block block = new Block(tok, body);
				return block;
			}
	
			case IF: {
				Token tok = token;
				eat(Tokens.IF);
				eat(Tokens.LEFTPAREN);
				Expression expr = parseExpression();
				eat(Tokens.RIGHTPAREN);
				Statement then = parseStatement();
				Statement elze = new Skip(then.getToken()); 
				if (token.getToken() == Tokens.ELSE) {
					eat(Tokens.ELSE);
					elze = parseStatement();
				}
				IfThenElse result = new IfThenElse(tok, expr, then, elze);
				return result;
			}
	
			case WHILE: {
				Token tok = token;
				eat(Tokens.WHILE);
				eat(Tokens.LEFTPAREN);
				Expression expr = parseExpression();
				eat(Tokens.RIGHTPAREN);
				Statement body = parseStatement();
				While result = new While(tok, expr, body);
				return result;
			}

			default:
				throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
    }

	/**
	 * Parses a statement with an Identifier and returns the corresponding Statement object.
	 * 
	 * @param id The Identifier for the statement.
	 * @return The parsed Statement object.
	 * @throws ParseException If there is an error while parsing the statement.
	 */
	private Statement parseState1(Identifier id) throws ParseException
	{
		switch (token.getToken()) {

		case ASSIGN: {
			Token tok = token;
			eat(Tokens.ASSIGN);
			Expression expr = parseExpression();
			eat(Tokens.SEMICOLON);
			Assign assign = new Assign(tok, id, expr);
			return assign;
		}

		case LEFTBRACKET: {
			eat(Tokens.LEFTBRACKET);
			Expression expr1 = parseExpression();
			eat(Tokens.RIGHTBRACKET);
			eat(Tokens.ASSIGN);
			Expression expr2 = parseExpression();
			eat(Tokens.SEMICOLON);
			ArrayAssign assign = new ArrayAssign(id.getToken(), id, expr1, expr2);
			return assign;
		}

		case LEFTPAREN: {
			IdentifierExpr idExpr = new IdentifierExpr(id.getToken(), id.getVarID());
			Token tok = token;
			List<Expression> exprList = new ArrayList<Expression>();

			eat(Tokens.LEFTPAREN);
			if (token.getToken() != Tokens.RIGHTPAREN) {
				Expression exprArg = parseExpression();
				exprList.add(exprArg);
				while (token.getToken() == Tokens.COMMA) {
					eat(Tokens.COMMA);
					exprArg = parseExpression();
					exprList.add(exprArg);
				}
			}
			eat(Tokens.RIGHTPAREN);
			eat(Tokens.SEMICOLON);
			CallFunctionStat callFunc = new CallFunctionStat(tok, null, idExpr, exprList);
			return callFunc;
		}

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
	}

	/**
	 * Parses a type and returns the corresponding Type object.
	 * 
	 * @return The parsed Type object.
	 * @throws ParseException If there is an error while parsing the type.
	 */
	private Type parseType() throws ParseException
	{
		switch (token.getToken()) {

		case INTEGER: {
			Token tok = token;
			eat(Tokens.INTEGER);
			Type type = parseType1();
			if (type == null) {
				return new IntType(tok);
			}
			return type;
		}

		case BOOLEAN: {
			BooleanType bt = new BooleanType(token);
			eat(Tokens.BOOLEAN);
			return bt;
		}
		case STRING: {
			StringType st = new StringType(token);
			eat(Tokens.STRING);
			return st;
		}

		case VOID: {
			VoidType vt = new VoidType(token);
			eat(Tokens.VOID);
			return vt;
		}

		case IDENTIFIER: {
			IdentifierType id = new IdentifierType(token, token.getSymbol());
			eat(Tokens.IDENTIFIER);
			return id;
		}

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
	}

	/**
	 * Parses a type and returns the corresponding Type object.
	 * 
	 * @return The parsed Type object.
	 * @throws ParseException If there is an error while parsing the type.
	 */
	private Type parseType1() throws ParseException
	{
		switch (token.getToken()) {

		case LEFTBRACKET: {
			Token tok = token;
			eat(Tokens.LEFTBRACKET);
			eat(Tokens.RIGHTBRACKET);
			IntArrayType type = new IntArrayType(tok);
			return type;
		}

		case IDENTIFIER: {
			return null;
		}

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
	}

	/**
	 * Parses an expression.
	 * 
	 * @throws ParseException If there is an error while parsing the expression.
	 */
	private void parseExpr() throws ParseException
	{
		switch (token.getToken()) {

		case INTEGER: {
			IntLiteral lit = new IntLiteral(token, Integer.parseInt(token.getSymbol()));
			eat(Tokens.INTEGER);
			stOperand.push(lit);
			parseTerm1();
		}
		break;

		case STRING: {
			StringLiteral sl = new StringLiteral(token, (String) token.getSymbol());
			eat(Tokens.STRING);
			stOperand.push(sl);
			parseTerm1();
		}
		break;

		case TRUE: {
			True true1 = new True(token);
			eat(Tokens.TRUE);
			stOperand.push(true1);
			parseTerm1();
		}
		break;

		case FALSE: {
			False false1 = new False(token);
			eat(Tokens.FALSE);
			stOperand.push(false1);
			parseTerm1();
		}
		break;

		case IDENTIFIER: {
			IdentifierExpr id = new IdentifierExpr(token, (String) token.getSymbol());
			eat(Tokens.IDENTIFIER);
			if (token.getToken() == Tokens.LEFTPAREN) {
				Expression expr = parseCallFunction(id);
				stOperand.push(expr);	
			} else {
				stOperand.push(id);
				parseTerm1();
			}
		}
		break;

		case NEW: {
			pushOperator(token);
			eat(Tokens.NEW);
			parseTerm2();
			parseTerm1();
		}
		break;

		case LEFTPAREN: {
			stOperand.add(parseFunctionBody());
		}
		break;

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token : " + token.getToken());
		}
	}

	/**
	 * Pushes an operator token onto the operator stack.
	 * 
	 * @param current The current operator token to be pushed.
	 */
	private void pushOperator(Token current)
	{
		Token top = stOperator.peek();
		while (getPriority(top.getToken()) >= getPriority(current.getToken())) {
			popOperator();
			top = stOperator.peek();
		}
		stOperator.push(current);
	}

	/**
	 * Pops an operator token from the operator stack and processes it accordingly.
	 */
	private void popOperator()
	{
		Token top = stOperator.pop();
		if (isBinary(top.getToken())) {
			parseBinary(top);
		} else {
			parseUnary(top);
		}
	}

	/**
	 * Parses an unary operation with the given token.
	 * 
	 * @param tok The unary operation token.
	 */
	private void parseUnary(Token tok)
	{
		switch (tok.getToken()) {
		case NEW: {
			Expression expr = stOperand.pop();
			IdentifierExpr idExpr = (IdentifierExpr) expr;
			NewInstance instance = new NewInstance(tok, idExpr);
			stOperand.push(instance);
		}
		break;

		default: {
			System.err.println("parseUnary(): Error in parsing");
		}
		break;
		}
	}

	/**
	 * Parses a binary operation with the given token.
	 * 
	 * @param tok The binary operation token.
	 */
	private void parseBinary(Token tok)
	{
		switch (tok.getToken()) {

		case OR: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Or or = new Or(tok, lhs, rhs);
			stOperand.push(or);
		}
		break;

		case AND: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			And and = new And(tok, lhs, rhs);
			stOperand.push(and);
		}
		break;

		case EQUALS: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Equals equals = new Equals(tok, lhs, rhs);
			stOperand.push(equals);
		}
		break;

		case LESSTHAN: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			LessThan lessThan = new LessThan(tok, lhs, rhs);
			stOperand.push(lessThan);
		}
		break;
		
		case PLUS: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Plus plus = new Plus(tok, lhs, rhs);
			stOperand.push(plus);
		}
		break;

		case MINUS: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Minus minus = new Minus(tok, lhs, rhs);
			stOperand.push(minus);
		}
		break;

		case TIMES: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Times times = new Times(tok, lhs, rhs);
			stOperand.push(times);
		}
		break;

		case DIV: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Division div = new Division(tok, lhs, rhs);
			stOperand.push(div);
		}
		break;

		case DOT: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			CallFunctionExpr cm = (CallFunctionExpr) rhs;
			cm.setInstanceName(lhs);
			stOperand.push(cm);
		}
		break;

		case LEFTBRACKET: {
			Expression indexExpr = stOperand.pop();
			Expression ArrayExpr = stOperand.pop();
			ArrayIndexExpr indexArray = new ArrayIndexExpr(ArrayExpr.getToken(), ArrayExpr, indexExpr);
			stOperand.push(indexArray);
		}
		break;

		default:
			System.err.println("parseBinary(): Error in parsing");
		}
	}

	/**
	 * Checks if the given operator is a binary operator.
	 * 
	 * @param operator The operator token to be checked.
	 * @return True if the operator is binary, false otherwise.
	 */
	private boolean isBinary(Tokens operator)
	{
		switch (operator) {

		case OR:
		case AND:
		case EQUALS:
		case LESSTHAN:
		case PLUS:
		case MINUS:
		case TIMES:
		case DIV:
		case DOT:
		case LEFTBRACKET: {
			return true;
		}
		case NEW: {
			return false;
		}
		default:
			return false;
		}
	}

	/**
	 * Returns the priority of the given operator.
	 * 
	 * @param operator The operator token to get the priority for.
	 * @return The priority of the operator.
	 */
	private int getPriority(Tokens operator)
	{
		switch (operator) {

		case SENTINEL: {
			return -1;
		}

		case OR: {
			return 1;
		}

		case AND: {
			return 2;
		}

		case EQUALS: {
			return 3;
		}

		case LESSTHAN: {
			return 3;
		}

		case PLUS: {
			return 4;
		}

		case MINUS: {
			return 4;
		}

		case TIMES: {
			return 5;
		}

		case DIV: {
			return 5;
		}

		case LEFTBRACKET: {
			return 7;
		}

		case DOT: {
			return 8;
		}

		case NEW: {
			return 9;
		}

		default:
			return 0;

		}

	}

	/**
	 * Parses the term following a binary operation.
	 * 
	 * @throws ParseException If there is an error while parsing the term.
	 */
	private void parseTerm1() throws ParseException
	{
		switch (token.getToken()) {

		case AND: {
			pushOperator(token);
			eat(Tokens.AND);
			parseExpr();
			parseTerm1();

		}
		break;

		case OR: {
			pushOperator(token);
			eat(Tokens.OR);
			parseExpr();
			parseTerm1();

		}
		break;

		case EQUALS: {
			pushOperator(token);
			eat(Tokens.EQUALS);
			parseExpr();
			parseTerm1();

		}
		break;

		case LESSTHAN: {
			pushOperator(token);
			eat(Tokens.LESSTHAN);
			parseExpr();
			parseTerm1();

		}
		break;

		case PLUS: {
			pushOperator(token);
			eat(Tokens.PLUS);
			parseExpr();
			parseTerm1();

		}
		break;

		case MINUS: {
			pushOperator(token);
			eat(Tokens.MINUS);
			parseExpr();
			parseTerm1();

		}
		break;

		case TIMES: {
			pushOperator(token);
			eat(Tokens.TIMES);
			parseExpr();
			parseTerm1();

		}
		break;

		case DIV: {
			pushOperator(token);
			eat(Tokens.DIV);
			parseExpr();
			parseTerm1();

		}
		break;

		case LEFTBRACKET: {
			pushOperator(token);
			eat(Tokens.LEFTBRACKET);
			stOperator.push(SENTINEL);
			Expression indexExpr = parseExpression();
			eat(Tokens.RIGHTBRACKET);
			stOperator.pop(); 
			stOperand.push(indexExpr);
			parseTerm1();
		}
		break;

		case RIGHTPAREN:
		case SEMICOLON:
		case COMMA:
		case RIGHTBRACKET: {
			// Epsilon expected
		}
		break;

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getSymbol());
		}
	}

	/**
	 * Parses the term following a new operation.
	 * 
	 * @throws ParseException If there is an error while parsing the term.
	 */
	private void parseTerm2() throws ParseException
	{
		switch (token.getToken()) {

		case INTEGER: {
			eat(Tokens.INTEGER);
			eat(Tokens.LEFTBRACKET);
			stOperator.push(SENTINEL);
			Expression arrayLength = parseExpression();
			eat(Tokens.RIGHTBRACKET);
			stOperator.pop(); 
			stOperator.pop(); 
			NewArray array = new NewArray(arrayLength.getToken(), arrayLength);
			stOperand.push(array);
		}
		break;

		case IDENTIFIER: {
			IdentifierExpr idExpr = new IdentifierExpr(token, token.getSymbol());
			eat(Tokens.IDENTIFIER);
			eat(Tokens.LEFTPAREN);
			eat(Tokens.RIGHTPAREN);
			stOperand.push(idExpr);
		}
		break;

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
	}

	/**
	 * Parses the access modifier.
	 * 
	 * @throws ParseException If there is an error while parsing the access modifier.
	 */
	private void parseAccess() throws ParseException
	{
		if (token.getToken() == Tokens.PUBLIC || token.getToken() == Tokens.PRIVATE || token.getToken() == Tokens.PROTECTED) {
			currentAccessModifier = token;
			eat(token.getToken());
			eat(Tokens.COLON);
		}
	}

	/**
	 * Advances the lexer to the next token.
	 */
	private void advance()
    {
        try {
            token = lexer.nextToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Consumes the current token if it matches the expected token, otherwise throws a ParseException.
	 * 
	 * @param tok The expected token to consume.
	 * @throws ParseException If the current token does not match the expected token.
	 */
    private void eat(Tokens tok) throws ParseException
    {
        if (tok == token.getToken()) {
            advance();
        } else {
            error(token.getToken(), tok);
        }
    }

	/**
	 * Throws a ParseException with an error message indicating an invalid token found and the expected token.
	 * 
	 * @param found The token that was found but is invalid.
	 * @param expected The token that was expected at the current position.
	 * @throws ParseException Always throws a ParseException with an error message.
	 */
    private void error(Tokens found, Tokens expected) throws ParseException
    {
        throw new ParseException(token.getRow(), token.getCol(), "Invalid token : " + found + " Expected token : " + expected);
    }
}