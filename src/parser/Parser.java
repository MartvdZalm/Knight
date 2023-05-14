package src.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import src.ast.*;
import src.lexer.*;

public class Parser 
{
    private Lexer lexer;
    private Token token;
    private static final Token SENTINEL = new Token(Symbol.symbol("SENTINEL", Tokens.SENTINEL), 0, 0);
    private Deque<Token> stOperator = new ArrayDeque<>();
    private Deque<Expression> stOperand = new ArrayDeque<>();
	private Token currentAccessModifier;

    public Parser(String sourceFile)
    {
        lexer = new Lexer(sourceFile);
        stOperator.push(SENTINEL);
    }

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
							includeList.add(parseInclude());
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

	private ClassDecl parseClassDecl() throws ParseException
	{
		eat(Tokens.CLASS);
		IdentifierExpr className = new IdentifierExpr(token, token.getSymbol());
		eat(Tokens.IDENTIFIER);

		IdentifierExpr parent = null;
		if (token.getToken() == Tokens.EXTENDS) {
			eat(Tokens.EXTENDS);
			if (token.getToken() == Tokens.IDENTIFIER) {
				parent = new IdentifierExpr(token, token.getSymbol());
			}
			eat(Tokens.IDENTIFIER);
		}
		eat(Tokens.LEFTBRACE);

		List<Declaration> varList = new ArrayList<>();
		List<FuncDecl> funcList = new ArrayList<>();
		
		parseAccess();

		while (token.getToken() != Tokens.RIGHTBRACE) {
			parseAccess();

			Type type = parseType();
			Identifier id = new Identifier(token, token.getSymbol());

			if (token.getToken() == Tokens.MAIN) {
				eat(Tokens.MAIN);
			} else {
				eat(Tokens.IDENTIFIER);
			}

			if (token.getToken() == Tokens.LEFTPAREN) {
				funcList.add(parseFuncDeclStandard(type, id));
			} else {
				varList.add(parseVariable(type, id));
			}
		}
		eat(Tokens.RIGHTBRACE);

		if (parent == null) {
			return new ClassDeclSimple(className.getToken(), className, varList, funcList);
		} else {
			return new ClassDeclExtends(className.getToken(), className, parent, varList, funcList);
		}
	}

	private FuncDecl parseFuncDeclStandard(Type returnType, Identifier identifier) throws ParseException
	{
		IdentifierExpr methodName = new IdentifierExpr(identifier.getToken(), identifier.getVarID());
		
		List<Declaration> varList = new ArrayList<>();
		List<Statement> statList = new ArrayList<>();
		List<ArgDecl> argList = new ArrayList<>();

		eat(Tokens.LEFTPAREN);
		if (token.getToken() != Tokens.RIGHTPAREN) {
			argList.add(parseArgument());

			while (token.getToken() == Tokens.COMMA) {
				eat(Tokens.COMMA);
				argList.add(parseArgument());
			}
		}
		eat(Tokens.RIGHTPAREN);
		eat(Tokens.LEFTBRACE);

		while (token.getToken() == Tokens.INTEGER || token.getToken() == Tokens.BOOLEAN || token.getToken() == Tokens.STRING) {
			Type type = parseType();
			Identifier id = new Identifier(token, token.getSymbol());
			eat(Tokens.IDENTIFIER);
			varList.add(parseVariable(type, id));
		}

		while (token.getToken() == Tokens.IDENTIFIER) {
			IdentifierType idType = new IdentifierType(token, token.getSymbol());
			Identifier id1 = new Identifier(token, token.getSymbol());
			eat(Tokens.IDENTIFIER);

			if (token.getToken() == Tokens.IDENTIFIER) { 
				Identifier id2 = new Identifier(token, token.getSymbol());
				eat(Tokens.IDENTIFIER);

				if (token.getToken() == Tokens.SEMICOLON) {
					varList.add(new VarDecl(id2.getToken(), idType, id2, currentAccessModifier));
				} else {
					varList.add(new VarDeclInit(token, idType, id2, parseExpression(), currentAccessModifier));
				}
				eat(Tokens.SEMICOLON);

				while (token.getToken() == Tokens.INTEGER || token.getToken() == Tokens.BOOLEAN || token.getToken() == Tokens.STRING) {
					Type type = parseType();
					Identifier id = new Identifier(token, token.getSymbol());
					eat(Tokens.IDENTIFIER);
					varList.add(parseVariable(type, id));
				}

			} else { 
				Statement stat = parseState1(id1);
				statList.add(stat);
				
				if (returnType.getToken().getToken() == Tokens.VOID) {
					while (token.getToken() != Tokens.RIGHTBRACE) {
						statList.add(parseStatement());
					}
				} else {
					while (token.getToken() != Tokens.RETURN) {
						statList.add(parseStatement());
					}
				}
			}
		}

		if (returnType.getToken().getToken() == Tokens.VOID) {
			while (token.getToken() != Tokens.RIGHTBRACE) {
				statList.add(parseStatement());
			}
			eat(Tokens.RIGHTBRACE);
			return new FuncDeclVoid(methodName.getToken(), returnType, methodName, argList, varList, statList, currentAccessModifier);			
		} else {
			while (token.getToken() != Tokens.RETURN) {
				statList.add(parseStatement());
			}
			eat(Tokens.RETURN);
			Expression returnExpr = parseExpression();
			eat(Tokens.SEMICOLON);	
			eat(Tokens.RIGHTBRACE);

			if (identifier.getToken().getToken() == Tokens.MAIN) {
				return new FuncDeclMain(token, returnType, methodName, varList, statList, returnExpr, currentAccessModifier);
			} else {
				return new FuncDeclReturn(methodName.getToken(), returnType, methodName, argList, varList, statList, returnExpr, currentAccessModifier);
			}
		}
	}

	private Include parseInclude() throws ParseException
	{
		Token tok = token;
		eat(Tokens.INCLUDE);
		eat(Tokens.LESSTHAN);
		IdentifierExpr className = new IdentifierExpr(token, token.getSymbol());
		eat(Tokens.IDENTIFIER);
		eat(Tokens.GREATERTHAN);

		return new Include(tok, className);
	}

	private Declaration parseVariable(Type type, Identifier id) throws ParseException
	{
		Declaration decl = null;

		if (token.getToken() == Tokens.SEMICOLON) {
			decl = new VarDecl(token, type, id, currentAccessModifier);
		} else {
			eat(Tokens.ASSIGN);
			decl = new VarDeclInit(token, type, id, parseExpression(), currentAccessModifier);
		}
		eat(Tokens.SEMICOLON);

		return decl;
	}

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
	
			case IDENTIFIER: {
				Identifier id = new Identifier(token, token.getSymbol());
				eat(Tokens.IDENTIFIER);
				Statement stat = parseState1(id);
				return stat;
			}

			default:
				throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
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
			return stOperand.pop();

        } catch (ParseException pe) {
            throw pe;
        } catch (Exception e) {
            System.err.println("Parser Error " + token.getRow() + ":" + token.getCol());
            throw e;
        }
    }

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

		case LEFTBRACE: {
			eat(Tokens.LEFTBRACE);
			Expression expr1 = parseExpression();
			eat(Tokens.RIGHTBRACE);
			eat(Tokens.ASSIGN);
			Expression expr2 = parseExpression();
			eat(Tokens.SEMICOLON);
			ArrayAssign assign = new ArrayAssign(id.getToken(), id, expr1, expr2);
			return assign;
		}

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
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

		case LEFTBRACE: {
			Token tok = token;
			eat(Tokens.LEFTBRACE);
			eat(Tokens.RIGHTBRACE);
			IntArrayType type = new IntArrayType(tok);
			return type;
		}

		case IDENTIFIER:
		case MAIN: {
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
			stOperand.push(id);
			parseTerm1();
		}
		break;

		case THIS: {
			This this1 = new This(token);
			eat(Tokens.THIS);
			stOperand.push(this1);
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

		case LEFTPAREN: {
			eat(Tokens.LEFTPAREN);
			stOperator.push(SENTINEL);
			Expression expr = parseExpression();
			eat(Tokens.RIGHTPAREN);
			stOperand.push(expr);
			stOperator.pop();
			parseTerm1();
		}
		break;

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
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
			System.err.println("parseUnary(): Error in parsing");
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
			if (rhs != null && rhs instanceof Length) {
				Length length = (Length) rhs;
				length.setArray(lhs);
				stOperand.push(length);
			} else if (rhs != null && rhs instanceof CallFunc) {
				// method call
				CallFunc cm = (CallFunc) rhs;
				cm.setInstanceName(lhs);
				stOperand.push(cm);
			}

		}
		break;

		case LEFTBRACE: {
			Expression indexExpr = stOperand.pop();
			Expression ArrayExpr = stOperand.pop();
			IndexArray indexArray = new IndexArray(ArrayExpr.getToken(), ArrayExpr, indexExpr);
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
		case PLUS:
		case MINUS:
		case TIMES:
		case DIV:
		case DOT:
		case LEFTBRACE: {
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

		case TIMES: {
			return 5;
		}

		case DIV: {
			return 5;
		}

		case LEFTBRACE: {
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

		case LEFTBRACE: {
			pushOperator(token);
			eat(Tokens.LEFTBRACE);
			stOperator.push(SENTINEL);
			Expression indexExpr = parseExpression();
			eat(Tokens.RIGHTBRACE);
			stOperator.pop(); 
			stOperand.push(indexExpr);
			parseTerm1();
		}
		break;

		case DOT: {
			pushOperator(token);
			eat(Tokens.DOT);
			parseTerm3();
			parseTerm1();
		}
		break;

		case RIGHTPAREN:
		case SEMICOLON:
		case COMMA:
		case RIGHTBRACE: {
			// Epsilon expected
		}
		break;

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getSymbol());

		}

	}

	private void parseTerm2() throws ParseException
	{
		switch (token.getToken()) {

		case INTEGER: {
			eat(Tokens.INTEGER);
			eat(Tokens.LEFTBRACE);
			stOperator.push(SENTINEL);
			Expression arrayLength = parseExpression();
			eat(Tokens.RIGHTBRACE);
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

	private void parseTerm3() throws ParseException
	{
		switch (token.getToken()) {

		case IDENTIFIER: {
			IdentifierExpr methodId = new IdentifierExpr(token, token.getSymbol());
			eat(Tokens.IDENTIFIER);
			eat(Tokens.LEFTPAREN);

			stOperator.push(SENTINEL);
			List<Expression> exprList = new ArrayList<Expression>();

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
			stOperator.pop(); // Pop SENTINEL
			CallFunc callMethod = new CallFunc(methodId.getToken(), null, methodId, exprList);
			stOperand.push(callMethod);
		}
		break;

		default:
			throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
		}
	}

	private void parseAccess() throws ParseException
	{
		if (token.getToken() == Tokens.PUBLIC) {
			currentAccessModifier = token;
			eat(Tokens.PUBLIC);
			eat(Tokens.COLON);
		} else if (token.getToken() == Tokens.PRIVATE) {
			currentAccessModifier = token;
			eat(Tokens.PRIVATE);
			eat(Tokens.COLON);
		} else if (token.getToken() == Tokens.PROTECTED){
			currentAccessModifier = token;
			eat(Tokens.PROTECTED);
			eat(Tokens.COLON);
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
