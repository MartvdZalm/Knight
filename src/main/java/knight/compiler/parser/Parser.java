package knight.compiler.parser;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import knight.compiler.ast.*;
import knight.compiler.ast.Class;
import knight.compiler.lexer.Lexer;
import knight.compiler.lexer.Symbol;
import knight.compiler.lexer.Token;
import knight.compiler.lexer.Tokens;

public class Parser
{
    public Lexer lexer;
    public Token token;
    private static final Token SENTINEL = new Token(Symbol.symbol("SENTINEL", Tokens.SENTINEL), 0, 0);
    private Deque<Token> stOperator = new ArrayDeque<>();
    private Deque<Expression> stOperand = new ArrayDeque<>();

    public Parser(BufferedReader bufferedReader)
    {
        lexer = new Lexer(bufferedReader);
        stOperator.push(SENTINEL);
    }

    public Tree parse() throws ParseException
    {
        Program program = null;

        try {
			List<Include> includeList = new ArrayList<>();
			List<Enumeration> enumerationList = new ArrayList<>();
			List<Interface> interfaceList = new ArrayList<>();
			List<Class> classList = new ArrayList<>();
			List<Function> functionList = new ArrayList<>();
			List<Variable> variableList = new ArrayList<>();

			token = lexer.nextToken();

			do {
				switch (token.getToken()) {
					case INCLUDE: {
						includeList.add(parseInclude());
					} break;

					case ENUMERATION: {
						enumerationList.add(parseEnumeration());
					} break;

					case INTERFACE: {
						interfaceList.add(parseInterface());
					} break;

					case CLASS: {
						classList.add(parseClass());
					} break;

					case FUNCTION: {
						functionList.add(parseFunction());
					} break;

					case INTEGER:
					case STRING: {
						variableList.add(parseVariable());
					} break;
					
					default: {
						throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
					}
                }
                
            } while (token != null);
			
            program = new Program(token, includeList, enumerationList, interfaceList, classList, functionList, variableList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return program;
    }

	public Include parseInclude() throws ParseException
	{
		eat(Tokens.INCLUDE);
		return new Include(token, parseIdentifier());
	}

	private Enumeration parseEnumeration() throws ParseException
	{
		eat(Tokens.ENUMERATION);
		Identifier id = parseIdentifier();
		eat(Tokens.LEFTBRACE);
		eat(Tokens.RIGHTBRACE);

		return new Enumeration(token, id);
	}

	private Interface parseInterface() throws ParseException
	{
		eat(Tokens.INTERFACE);
		Identifier id = parseIdentifier();
		eat(Tokens.LEFTBRACE);
		eat(Tokens.RIGHTBRACE);

		return new Interface(token, id);
	}

	public Class parseClass() throws ParseException
	{
		List<Function> functions = new ArrayList<>();
		List<Variable> variables = new ArrayList<>();

		eat(Tokens.CLASS);
		Identifier id = parseIdentifier();
		eat(Tokens.LEFTBRACE);

		while (token.getToken() != Tokens.RIGHTBRACE) {
			if (token.getToken() == Tokens.FUNCTION) {
				functions.add(parseFunction());
			} else {
				variables.add(parseVariable());
			}
		}
		eat(Tokens.RIGHTBRACE);
		return new Class(id.getToken(), id, functions, variables);
	}

	public Function parseFunction() throws ParseException
	{
		List<Variable> variables = new ArrayList<>();
		List<Statement> statements = new ArrayList<>();

		eat(Tokens.FUNCTION);
		Identifier id = parseIdentifier();
		List<Argument> argumentList = parseArguments();
		eat(Tokens.COLON);
		Type returnType = parseType();
		eat(Tokens.LEFTBRACE);

		while (token.getToken() != Tokens.RIGHTBRACE && token.getToken() != Tokens.RETURN) {
			if (token.getToken() == Tokens.INTEGER || token.getToken() == Tokens.STRING) {
				variables.add(parseVariable());
			} else {
				statements.add(parseStatement());
			}
		}

		Expression returnExpr = parseReturnExpr();
		eat(Tokens.RIGHTBRACE);

		if (returnExpr != null) {
			return new FunctionReturn(token, returnType, id, argumentList, variables, statements, returnExpr);
		}

		return new Function(token, returnType, id, argumentList, variables, statements);
	}

	private Variable parseVariable() throws ParseException
	{
		Type type = parseType();
		Identifier id = parseIdentifier();

		Variable variable = null;
		if (token.getToken() == Tokens.SEMICOLON) {
			variable = new Variable(token, type, id);
			eat(Tokens.SEMICOLON);
		} else {
			eat(Tokens.ASSIGN);
			variable = new VariableInit(token, type, id, parseExpression());
		}
		return variable;
	}

	private Expression parseReturnExpr() throws ParseException
	{
		if (token.getToken() == Tokens.RETURN) {
			eat(Tokens.RETURN);
			return parseExpression();
		}
		return null;
	}

	private Expression parseExpression() throws ParseException
    {
        try {
            parseExpr();
            Token tok = stOperator.peek();
			while (tok.getToken() != Tokens.SENTINEL) {
				popOperator();
				tok = stOperator.peek();
			}

			if (token.getToken() == Tokens.SEMICOLON) {
				eat(Tokens.SEMICOLON);
			}

			return stOperand.pop();

        } catch (ParseException pe) {
            throw pe;
        } catch (Exception e) {
            System.err.println("Parser Error " + token.getRow() + ":" + token.getCol());
            throw e;
        }
    }

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

	private List<Argument> parseArguments() throws ParseException
	{
		List<Argument> argumentList = new ArrayList<>();
		eat(Tokens.LEFTPAREN);
		if (token.getToken() != Tokens.RIGHTPAREN) {
			argumentList.add(parseArgument());

			while (token.getToken() == Tokens.COMMA) {
				eat(Tokens.COMMA);;
				argumentList.add(parseArgument());
			}
		}
		eat(Tokens.RIGHTPAREN);
		return argumentList;
	}

	private Argument parseArgument() throws ParseException
	{
		Identifier argumentId = null;
		Type argumentType = parseType();
		if (token.getToken() == Tokens.IDENTIFIER) {
			argumentId = new Identifier(token, token.getSymbol());
		}
		eat(Tokens.IDENTIFIER);

		return new Argument(argumentId.getToken(), argumentType, argumentId);
	}

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

			case FOR: {
				Token tok = token;
				eat(Tokens.FOR);
				eat(Tokens.LEFTPAREN);
				Variable initialization = parseVariable();
				Expression condition = parseExpression();
				Statement increment = parseStatement();
				eat(Tokens.RIGHTPAREN);
				Statement body = parseStatement();
				ForLoop forLoop = new ForLoop(tok, initialization, condition, increment, body);
				return forLoop;
			}

			case RETURN: {
				Token tok = token;
				eat(Tokens.RETURN);
				Expression returnExpr = parseExpression();
				ReturnStatement returnStatement = new ReturnStatement(tok, returnExpr);
				return returnStatement;
			}

			case IDENTIFIER: {
				Identifier id = parseIdentifier();
				return parseState(id);
			}

			default:
				throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
    }

	private Statement parseState(Identifier id) throws ParseException
	{
		switch (token.getToken()) {

		case ASSIGN: {
			Token tok = token;
			eat(Tokens.ASSIGN);
			Expression expr = parseExpression();
			Assign assign = new Assign(tok, id, expr);
			return assign;
		}

		case LEFTBRACKET: {
			eat(Tokens.LEFTBRACKET);
			Expression expr1 = parseExpression();
			eat(Tokens.RIGHTBRACKET);
			eat(Tokens.ASSIGN);
			Expression expr2 = parseExpression();
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

	private Identifier parseIdentifier() throws ParseException
	{
		checkNotNull(token);

		Identifier id = new Identifier(token, token.getSymbol());
		eat(Tokens.IDENTIFIER);
		return id;
	}

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

		case FUNCTION: {
			FunctionType functionType = new FunctionType(token);
			eat(Tokens.FUNCTION);
			return functionType;
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

		case IDENTIFIER:
		case LEFTBRACE:
		case ASSIGN:
		case RIGHTPAREN:
		case COMMA: {
			return null;
		}

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
	}

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
			}
			parseTerm1();
		}
		break;

		case NEW: {
			pushOperator(token);
			eat(Tokens.NEW);
			parseTerm2();
			parseTerm1();
		}
		break;

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token : " + token.getToken());
		}
	}

	private void pushOperator(Token current)
	{
		Token top = stOperator.peek();
		while (getPriority(top.getToken()) >= getPriority(current.getToken())) {
			popOperator();
			top = stOperator.peek();
		}
		stOperator.push(current);
	}

	private void popOperator()
	{
		Token top = stOperator.pop();
		if (isBinary(top.getToken())) {
			parseBinary(top);
		} else {
			parseUnary(top);
		}
	}

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
			System.err.println("parseUnary(): Error in parsing " + token.getToken() + " " + token.getRow());
		}
		break;
		}
	}

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

		case LESSTHANOREQUAL: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			LessThanOrEqual lessThanOrEqual = new LessThanOrEqual(tok, lhs, rhs);
			stOperand.push(lessThanOrEqual);
		}
		break;

		case GREATERTHAN: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			GreaterThan GreaterThan = new GreaterThan(tok, lhs, rhs);
			stOperand.push(GreaterThan);
		}
		break;

		case GREATERTHANOREQUAL: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			GreaterThanOrEqual greaterThanOrEqual = new GreaterThanOrEqual(tok, lhs, rhs);
			stOperand.push(greaterThanOrEqual);
		}
		break;
		
		case PLUS: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Plus plus = new Plus(tok, lhs, rhs);
			stOperand.push(plus);
		}
		break;

		case INCREMENT: {
			Expression expr = stOperand.pop();
			Increment inc = new Increment(tok, expr);
			stOperand.push(inc);
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

		case MODULUS: {
			Expression rhs = stOperand.pop();
			Expression lhs = stOperand.pop();
			Modulus modulus = new Modulus(tok, lhs, rhs);
			stOperand.push(modulus);
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

	private boolean isBinary(Tokens operator)
	{
		switch (operator) {

		case OR:
		case AND:
		case EQUALS:
		case LESSTHAN:
		case LESSTHANOREQUAL:
		case GREATERTHAN:
		case GREATERTHANOREQUAL:
		case PLUS:
		case INCREMENT:
		case MINUS:
		case TIMES:
		case DIV:
		case MODULUS:
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

		case MODULUS: {
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

		case LESSTHANOREQUAL: {
			pushOperator(token);
			eat(Tokens.LESSTHANOREQUAL);
			parseExpr();
			parseTerm1();
		}
		break;

		case GREATERTHAN: {
			pushOperator(token);
			eat(Tokens.GREATERTHAN);
			parseExpr();
			parseTerm1();
		}
		break;

		case GREATERTHANOREQUAL: {
			pushOperator(token);
			eat(Tokens.GREATERTHANOREQUAL);
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

		case INCREMENT: {
			pushOperator(token);
			eat(Tokens.INCREMENT);
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

		case MODULUS: {
			pushOperator(token);
			eat(Tokens.MODULUS);
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
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
	}

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

	private void checkNotNull(Token token) throws ParseException
	{
	    if (token == null) {
	        throw new ParseException(0, 0, "Token is null, cannot perform operation.");
	    }
	}

	private void advance()
    {
        try {
            token = lexer.nextToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eat(Tokens tok) throws ParseException
    {
        if (tok == token.getToken()) {
            advance();
        } else {
            error(token.getToken(), tok);
        }
    }

    private void error(Tokens found, Tokens expected) throws ParseException
    {
        throw new ParseException(token.getRow(), token.getCol(), "Invalid token : " + found + " Expected token : " + expected);
    }
}