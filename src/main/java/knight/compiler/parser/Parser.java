package knight.compiler.parser;

import java.util.*;

import knight.compiler.ast.*;
import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTForeach;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.program.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.types.ASTBooleanType;
import knight.compiler.ast.types.ASTIdentifierType;
import knight.compiler.ast.types.ASTIntArrayType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTParameterizedType;
import knight.compiler.ast.types.ASTStringArrayType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.ast.types.ASTType;
import knight.compiler.ast.types.ASTVoidType;
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
	private Deque<ASTExpression> stOperand = new ArrayDeque<>();

	public Parser(Lexer lexer)
	{
		this.lexer = lexer;
		stOperator.push(SENTINEL);
	}

	public AST parse() throws ParseException
	{
		List<ASTImport> importList = new ArrayList<>();
		List<AST> nodeList = new ArrayList<>();

		try {
			token = lexer.nextToken();

			do {
				boolean isAbstract = matchAndConsume(Tokens.ABSTRACT);
				boolean isStatic = matchAndConsume(Tokens.STATIC);

				switch (token.getToken())
				{
					case IMPORT: {
						importList = parseImports();
					}
					break;

					case CLASS: {
						nodeList.add(parseClass(isAbstract, isStatic));
					}
					break;

					case INTERFACE: {
						nodeList.add(parseInterface());
					}
					break;

					case FUNCTION: {
						nodeList.add(parseFunction(isAbstract, isStatic));
					}
					break;

					case INTEGER:
					case STRING:
					case BOOLEAN:
					case IDENTIFIER: {
						nodeList.add(parseVariable(isStatic));
					}
					break;

					default: {
						throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
					}
				}

			} while (token.getToken() != Tokens.EOF);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ASTProgram(token, importList, nodeList);
	}

	public List<ASTImport> parseImports() throws ParseException
	{
		List<ASTImport> importList = new ArrayList<>();

		while (token.getToken() == Tokens.IMPORT) {
			Token tok = token;
			eat(Tokens.IMPORT);
			importList.add(new ASTImport(tok, this.parseIdentifier()));
		}

		return importList;
	}

	public ASTInterface parseInterface() throws ParseException
	{
		eat(Tokens.INTERFACE);
		ASTIdentifier name = parseIdentifier();

		List<ASTIdentifier> extendsInterfaces = new ArrayList<>();
		if (token.getToken() == Tokens.EXTENDS) {
			eat(Tokens.EXTENDS);
			extendsInterfaces.add(parseIdentifier());
			while (token.getToken() == Tokens.COMMA) {
				eat(Tokens.COMMA);
				extendsInterfaces.add(parseIdentifier());
			}
		}

		eat(Tokens.LEFTBRACE);

		List<ASTFunction> methods = new ArrayList<>();
		while (token.getToken() != Tokens.RIGHTBRACE) {
			methods.add(parseInterfaceMethod());
		}

		eat(Tokens.RIGHTBRACE);
		return new ASTInterface(token, name, methods, extendsInterfaces);
	}

	private ASTFunction parseInterfaceMethod() throws ParseException
	{
		eat(Tokens.FUNCTION);
		ASTIdentifier name = parseIdentifier();
		List<ASTArgument> params = parseArguments();
		eat(Tokens.COLON);
		ASTType returnType = parseType();

		ASTBody body = null;
		eat(Tokens.SEMICOLON);

		return new ASTFunction(token, returnType, name, params, body, false, false);
	}

	public ASTClass parseClass(boolean isAbstract, boolean isStatic) throws ParseException
	{
		List<ASTProperty> propertyList = new ArrayList<>();
		List<ASTFunction> functionList = new ArrayList<>();
		ASTIdentifier extendsClass = null;
		List<ASTIdentifier> implementsInterfaces = new ArrayList<>();

		eat(Tokens.CLASS);
		ASTIdentifier className = parseIdentifier();

		if (matchAndConsume(Tokens.EXTENDS)) {
			extendsClass = parseIdentifier();
		}

		if (matchAndConsume(Tokens.IMPLEMENTS)) {
			do {
				implementsInterfaces.add(parseIdentifier());
			} while (matchAndConsume(Tokens.COMMA));
		}

		eat(Tokens.LEFTBRACE);

		while (token.getToken() != Tokens.RIGHTBRACE) {
			boolean memberAbstract = matchAndConsume(Tokens.ABSTRACT);
			boolean memberStatic = matchAndConsume(Tokens.STATIC);

			if (token.getToken() == Tokens.FUNCTION) {
				functionList.add(parseFunction(memberAbstract, memberStatic));
			} else {
				propertyList.add(parseProperty(memberStatic));
			}
		}
		eat(Tokens.RIGHTBRACE);

		return new ASTClass(className.getToken(), className, propertyList, functionList, extendsClass,
				implementsInterfaces, isAbstract, isStatic);
	}

	public ASTFunction parseFunction(boolean isAbstract, boolean isStatic) throws ParseException
	{
		eat(Tokens.FUNCTION);
		ASTIdentifier id = parseIdentifier();
		List<ASTArgument> argumentList = parseArguments();
		eat(Tokens.COLON);
		ASTType returnType = parseType();
		eat(Tokens.LEFTBRACE);

		ASTBody body = this.parseBody();

		eat(Tokens.RIGHTBRACE);

		return new ASTFunction(token, returnType, id, argumentList, body, isAbstract, isStatic);
	}

	private ASTBody parseBody() throws ParseException
	{
		Token tok = token;
		List<AST> nodes = new ArrayList<>();

		while (token.getToken() != Tokens.RIGHTBRACE) {
			boolean isStatic = matchAndConsume(Tokens.STATIC);
			boolean isAbstract = matchAndConsume(Tokens.ABSTRACT);

			switch (token.getToken())
			{
				case WHILE:
				case FOR:
				case RETURN:
				case IF: {
					nodes.add(parseStatement());
				}
				break;

				case INTEGER:
				case STRING:
				case BOOLEAN: {
					nodes.add(parseVariable(isStatic));
				}
				break;

				case FUNCTION: {
					nodes.add(parseFunction(isAbstract, isStatic));
				}
				break;

				case IDENTIFIER: {
					if (peek().getToken() != Tokens.ASSIGN && peek().getToken() != Tokens.LEFTPAREN
							&& peek().getToken() != Tokens.DOT) {
						nodes.add(parseVariable(isStatic));
					} else {
						nodes.add(parseStatement());
					}
				}
				break;

				default: {
					throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
				}
			}
		}

		return new ASTBody(tok, nodes);
	}

	public ASTProperty parseProperty(boolean isStatic) throws ParseException
	{
		ASTType type = parseType();
		ASTIdentifier identifier = parseIdentifier();

		ASTExpression expression = null;
		if (token.getToken() == Tokens.ASSIGN) {
			eat(Tokens.ASSIGN);
			expression = parseExpression();
		}

		eat(Tokens.SEMICOLON);
		return new ASTProperty(token, type, identifier, expression, isStatic);
	}

	public ASTVariable parseVariable(boolean isStatic) throws ParseException
	{
		ASTType type = parseType();
		ASTIdentifier id = parseIdentifier();
		ASTVariable variable = null;

		if (checkNotNull(token).getToken() == Tokens.SEMICOLON) {
			variable = new ASTVariable(token, type, id, isStatic);
			eat(Tokens.SEMICOLON);
		} else if (checkNotNull(token).getToken() == Tokens.ASSIGN) {
			eat(Tokens.ASSIGN);
			ASTExpression expr = parseExpression();
			variable = new ASTVariableInit(token, type, id, expr, isStatic);
		} else {
			variable = new ASTVariable(token, type, id, isStatic);
		}

		return variable;
	}

	public ASTExpression parseExpression() throws ParseException
	{
		try {
			parseExpr();
			Token tok = stOperator.peek();
			while (tok.getToken() != Tokens.SENTINEL) {
				popOperator();
				tok = stOperator.peek();
			}

			if (checkNotNull(token).getToken() == Tokens.SEMICOLON) {
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

	public List<ASTArgument> parseArguments() throws ParseException
	{
		List<ASTArgument> argumentList = new ArrayList<>();
		eat(Tokens.LEFTPAREN);
		if (token.getToken() != Tokens.RIGHTPAREN) {
			argumentList.add(parseArgument());

			while (token.getToken() == Tokens.COMMA) {
				eat(Tokens.COMMA);
				argumentList.add(parseArgument());
			}
		}
		eat(Tokens.RIGHTPAREN);
		return argumentList;
	}

	public ASTArgument parseArgument() throws ParseException
	{
		ASTType type = parseType();
		ASTIdentifier identifier = parseIdentifier();
		return new ASTArgument(token, type, identifier);
	}

	public ASTStatement parseStatement() throws ParseException
	{
		switch (token.getToken())
		{

			case LEFTBRACE: {
				ASTBody body = this.parseBody();
				return body;
			}

			case IF: {
				Token tok = token;
				eat(Tokens.IF);
				eat(Tokens.LEFTPAREN);
				ASTExpression condition = parseExpression();
				eat(Tokens.RIGHTPAREN);
				eat(Tokens.LEFTBRACE);

				ASTBody ifBody = this.parseBody();
				eat(Tokens.RIGHTBRACE);

				List<ASTConditionalBranch> branches = new ArrayList<>();
				branches.add(new ASTConditionalBranch(tok, condition, ifBody));

				ASTBody elseBody = null;

				while (token.getToken() == Tokens.ELSE) {
					eat(Tokens.ELSE);

					if (token.getToken() == Tokens.IF) {
						eat(Tokens.IF);
						eat(Tokens.LEFTPAREN);
						condition = parseExpression();
						eat(Tokens.RIGHTPAREN);
						eat(Tokens.LEFTBRACE);
						ASTBody elseIfBody = this.parseBody();
						eat(Tokens.RIGHTBRACE);

						branches.add(new ASTConditionalBranch(tok, condition, elseIfBody));
					} else {
						eat(Tokens.LEFTBRACE);
						elseBody = parseBody();
						eat(Tokens.RIGHTBRACE);
						break;
					}
				}
				ASTIfChain result = new ASTIfChain(tok, branches, elseBody);

				return result;
			}

			case FOR: {
				Token tok = token;
				eat(Tokens.FOR);
				eat(Tokens.LEFTPAREN);
				ASTVariable variable = this.parseVariable(false);
				eat(Tokens.COLON);
				ASTExpression iterable = this.parseExpression();
				eat(Tokens.RIGHTPAREN);
				eat(Tokens.LEFTBRACE);
				ASTBody body = this.parseBody();
				eat(Tokens.RIGHTBRACE);
				return new ASTForeach(tok, variable, iterable, body);
			}

			case WHILE: {
				Token tok = token;
				eat(Tokens.WHILE);
				eat(Tokens.LEFTPAREN);
				ASTExpression expr = this.parseExpression();
				eat(Tokens.RIGHTPAREN);
				eat(Tokens.LEFTBRACE);
				ASTBody body = this.parseBody();
				eat(Tokens.RIGHTBRACE);
				ASTWhile result = new ASTWhile(tok, expr, body);
				return result;
			}

			case RETURN: {
				Token tok = token;
				eat(Tokens.RETURN);
				ASTExpression returnExpr = parseExpression();
				ASTReturnStatement returnStatement = new ASTReturnStatement(tok, returnExpr);
				return returnStatement;
			}

			case IDENTIFIER: {
				ASTIdentifier id = parseIdentifier();

				switch (token.getToken())
				{
					case ASSIGN: {
						Token tok = token;
						eat(Tokens.ASSIGN);
						ASTExpression expr = parseExpression();
						ASTAssign assign = new ASTAssign(tok, id, expr);
						return assign;
					}

					case LEFTBRACKET: {
						eat(Tokens.LEFTBRACKET);
						ASTExpression expr1 = parseExpression();
						eat(Tokens.RIGHTBRACKET);
						eat(Tokens.ASSIGN);
						ASTExpression expr2 = parseExpression();
						ASTArrayAssign assign = new ASTArrayAssign(id.getToken(), id, expr1, expr2);
						return assign;
					}

					case LEFTPAREN: {
						ASTIdentifierExpr idExpr = new ASTIdentifierExpr(id.getToken(), id.getId());
						Token tok = token;
						List<ASTExpression> exprList = new ArrayList<ASTExpression>();

						eat(Tokens.LEFTPAREN);
						if (token.getToken() != Tokens.RIGHTPAREN) {
							ASTExpression exprArg = parseExpression();
							exprList.add(exprArg);
							while (token.getToken() == Tokens.COMMA) {
								eat(Tokens.COMMA);
								exprArg = parseExpression();
								exprList.add(exprArg);
							}
						}
						eat(Tokens.RIGHTPAREN);
						eat(Tokens.SEMICOLON);
						ASTCallFunctionStat callFunc = new ASTCallFunctionStat(tok, null, idExpr, exprList);
						return callFunc;
					}

					case DOT: {
						Token tok = token;

						ASTIdentifierExpr instance = new ASTIdentifierExpr(id.getToken(), id.getId());
						eat(Tokens.DOT);
						ASTIdentifierExpr functionName = new ASTIdentifierExpr(token, checkNotNull(token).getSymbol());
						eat(Tokens.IDENTIFIER);

						List<ASTExpression> exprList = new ArrayList<ASTExpression>();

						eat(Tokens.LEFTPAREN);
						if (token.getToken() != Tokens.RIGHTPAREN) {
							ASTExpression exprArg = parseExpression();
							exprList.add(exprArg);
							while (token.getToken() == Tokens.COMMA) {
								eat(Tokens.COMMA);
								exprArg = parseExpression();
								exprList.add(exprArg);
							}
						}
						eat(Tokens.RIGHTPAREN);
						eat(Tokens.SEMICOLON);
						ASTCallFunctionStat callFunc = new ASTCallFunctionStat(tok, instance, functionName, exprList);
						return callFunc;
					}

					default:
						throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
				}
			}

			default: {
				throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
			}
		}
	}

	public ASTIdentifier parseIdentifier() throws ParseException
	{
		ASTIdentifier id = new ASTIdentifier(token, checkNotNull(token).getSymbol());
		eat(Tokens.IDENTIFIER);
		return id;
	}

	public ASTType parseType() throws ParseException
	{
		switch (token.getToken())
		{
			case INTEGER: {
				Token tok = token;
				eat(Tokens.INTEGER);

				if (checkNotNull(token).getToken() == Tokens.LEFTBRACKET) {
					tok = token;
					eat(Tokens.LEFTBRACKET);
					eat(Tokens.RIGHTBRACKET);
					return new ASTIntArrayType(tok);
				}

				return new ASTIntType(tok);
			}

			case STRING: {
				Token tok = token;
				eat(Tokens.STRING);

				if (checkNotNull(token).getToken() == Tokens.LEFTBRACKET) {
					tok = token;
					eat(Tokens.LEFTBRACKET);
					eat(Tokens.RIGHTBRACKET);
					return new ASTStringArrayType(tok);
				}

				return new ASTStringType(tok);
			}

			case BOOLEAN: {
				ASTBooleanType booleanType = new ASTBooleanType(token);
				eat(Tokens.BOOLEAN);
				return booleanType;
			}

			case IDENTIFIER: {
				ASTIdentifierType identifierType = new ASTIdentifierType(token, token.getSymbol());
				eat(Tokens.IDENTIFIER);

				if (token.getToken() == Tokens.LESSTHAN) {
					Token tok = token;
					eat(Tokens.LESSTHAN);
					List<ASTType> templateArguments = new ArrayList<>();
					while (token.getToken() != Tokens.GREATERTHAN) {
						templateArguments.add(this.parseType());
						if (token.getToken() == Tokens.COMMA) {
							eat(Tokens.COMMA);
						}
					}
					eat(Tokens.GREATERTHAN);
					return new ASTParameterizedType(tok, identifierType, templateArguments);
				}

				return identifierType;
			}

			case VOID: {
				ASTVoidType voidType = new ASTVoidType(token);
				eat(Tokens.VOID);
				return voidType;
			}

			default: {
				throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
			}
		}
	}

	public void parseExpr() throws ParseException
	{
		switch (token.getToken())
		{
			case INTEGER: {
				ASTIntLiteral lit = new ASTIntLiteral(token, Integer.parseInt(token.getSymbol()));
				eat(Tokens.INTEGER);
				stOperand.push(lit);
				parseTerm1();
			}
			break;

			case STRING: {
				ASTStringLiteral sl = new ASTStringLiteral(token, (String) token.getSymbol());
				eat(Tokens.STRING);
				stOperand.push(sl);
				parseTerm1();
			}
			break;

			case TRUE: {
				ASTTrue true1 = new ASTTrue(token);
				eat(Tokens.TRUE);
				stOperand.push(true1);
				parseTerm1();
			}
			break;

			case FALSE: {
				ASTFalse false1 = new ASTFalse(token);
				eat(Tokens.FALSE);
				stOperand.push(false1);
				parseTerm1();
			}
			break;

			case IDENTIFIER: {
				ASTIdentifierExpr id = new ASTIdentifierExpr(token, (String) token.getSymbol());
				eat(Tokens.IDENTIFIER);
				if (token.getToken() == Tokens.LEFTPAREN || token.getToken() == Tokens.DOT) {
					ASTExpression expr = parseCallFunction(id);
					stOperand.push(expr);
				} else {
					stOperand.push(id);
				}
				parseTerm1();
			}
			break;

			case FUNCTION: {
				Token tok = token;
				eat(Tokens.FUNCTION);
				List<ASTArgument> arguments = this.parseArguments();
				eat(Tokens.COLON);
				ASTType returnType = parseType();
				eat(Tokens.LEFTBRACE);
				ASTBody body = this.parseBody();
				eat(Tokens.RIGHTBRACE);
				stOperand.push(new ASTLambda(tok, returnType, arguments, body));
			}
			break;

			case LEFTBRACE: {
				Token tok = token;
				eat(Tokens.LEFTBRACE);
				List<ASTExpression> elements = new ArrayList<>();

				if (token.getToken() != Tokens.RIGHTBRACE) {
					elements.add(parseExpression());
					while (token.getToken() == Tokens.COMMA) {
						eat(Tokens.COMMA);
						elements.add(parseExpression());
					}
				}
				eat(Tokens.RIGHTBRACE);
				ASTArrayLiteral arrayLiteral = new ASTArrayLiteral(tok, elements);
				stOperand.push(arrayLiteral);
			}
			break;

			case NEW: {
				pushOperator(token);
				eat(Tokens.NEW);

				switch (token.getToken())
				{
					case INTEGER: {
						eat(Tokens.INTEGER);
						eat(Tokens.LEFTBRACKET);
						stOperator.push(SENTINEL);
						ASTExpression arrayLength = parseExpression();
						eat(Tokens.RIGHTBRACKET);
						stOperator.pop();
						stOperator.pop();
						ASTNewArray array = new ASTNewArray(arrayLength.getToken(), arrayLength);
						stOperand.push(array);
					}
					break;

					case STRING: {
						eat(Tokens.STRING);
						eat(Tokens.LEFTBRACKET);
						stOperator.push(SENTINEL);
						ASTExpression arrayLength = parseExpression();
						eat(Tokens.RIGHTBRACKET);
						stOperator.pop();
						stOperator.pop();
						ASTNewArray array = new ASTNewArray(arrayLength.getToken(), arrayLength);
						stOperand.push(array);
					}
					break;

					case IDENTIFIER: {
						// ASTExpression expr = stOperand.pop();
						// ASTIdentifierExpr idExpr = (ASTIdentifierExpr) expr;
						//
						// List<ASTArgument> arguments = this.parseArguments();
						//
						// ASTNewInstance instance = new ASTNewInstance(tok, idExpr, arguments);
						// stOperand.push(instance);

						ASTIdentifierExpr idExpr = new ASTIdentifierExpr(token, token.getSymbol());
						eat(Tokens.IDENTIFIER);
						List<ASTArgument> arguments = this.parseArguments();
						stOperand.push(new ASTNewInstance(token, idExpr, arguments));
					}
					break;

					default: {
						throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
					}
				}

				parseTerm1();
			}
			break;

			default: {
				throw new ParseException(token.getRow(), token.getCol(), "Invalid token : " + token.getToken());
			}
		}
	}

	public ASTExpression parseCallFunction(ASTIdentifierExpr id) throws ParseException
	{
		Token tok = token;
		List<ASTExpression> exprList = new ArrayList<ASTExpression>();

		ASTIdentifierExpr instance = null;
		ASTIdentifierExpr function = null;

		if (token.getToken() == Tokens.DOT) {
			instance = id;
			eat(Tokens.DOT);
			function = new ASTIdentifierExpr(token, (String) token.getSymbol());
			eat(Tokens.IDENTIFIER);
		} else {
			function = id;
		}

		eat(Tokens.LEFTPAREN);
		if (token.getToken() != Tokens.RIGHTPAREN) {
			ASTExpression exprArg = parseExpression();
			exprList.add(exprArg);
			while (token.getToken() == Tokens.COMMA) {
				eat(Tokens.COMMA);
				exprArg = parseExpression();
				exprList.add(exprArg);
			}
		}

		eat(Tokens.RIGHTPAREN);
		return new ASTCallFunctionExpr(tok, instance, function, exprList);
	}

	public void pushOperator(Token current) throws ParseException
	{
		Token top = stOperator.peek();

		while (getPriority(top.getToken()) >= getPriority(current.getToken())) {
			popOperator();
			top = stOperator.peek();
		}

		stOperator.push(current);
	}

	public void popOperator() throws ParseException
	{
		Token top = stOperator.pop();

		if (isBinary(top.getToken())) {
			parseBinary(top);
		} else {
			parseUnary(top);
		}
	}

	public void parseUnary(Token tok) throws ParseException
	{
//		if (Objects.requireNonNull(tok.getToken()) == Tokens.NEW) {
//			ASTExpression expr = stOperand.pop();
//			ASTIdentifierExpr idExpr = (ASTIdentifierExpr) expr;
//
//			List<ASTArgument> arguments = this.parseArguments();
//
//			ASTNewInstance instance = new ASTNewInstance(tok, idExpr, arguments);
//			stOperand.push(instance);
//		} else {
//			System.err.println("parseUnary(): Error in parsing " + token.getToken() + " " + token.getRow());
//		}
	}

	public void parseBinary(Token tok)
	{
		switch (tok.getToken())
		{
			case OR: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTOr or = new ASTOr(tok, lhs, rhs);
				stOperand.push(or);
			}
			break;

			case AND: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTAnd and = new ASTAnd(tok, lhs, rhs);
				stOperand.push(and);
			}
			break;

			case EQUALS: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTEquals equals = new ASTEquals(tok, lhs, rhs);
				stOperand.push(equals);
			}
			break;

			case NOTEQUALS: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTNotEquals notEquals = new ASTNotEquals(tok, lhs, rhs);
				stOperand.push(notEquals);
			}
			break;

			case LESSTHAN: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTLessThan lessThan = new ASTLessThan(tok, lhs, rhs);
				stOperand.push(lessThan);
			}
			break;

			case LESSTHANOREQUAL: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTLessThanOrEqual lessThanOrEqual = new ASTLessThanOrEqual(tok, lhs, rhs);
				stOperand.push(lessThanOrEqual);
			}
			break;

			case GREATERTHAN: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTGreaterThan greaterThan = new ASTGreaterThan(tok, lhs, rhs);
				stOperand.push(greaterThan);
			}
			break;

			case GREATERTHANOREQUAL: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTGreaterThanOrEqual greaterThanOrEqual = new ASTGreaterThanOrEqual(tok, lhs, rhs);
				stOperand.push(greaterThanOrEqual);
			}
			break;

			case PLUS: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTPlus plus = new ASTPlus(tok, lhs, rhs);
				stOperand.push(plus);
			}
			break;

			case MINUS: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTMinus minus = new ASTMinus(tok, lhs, rhs);
				stOperand.push(minus);
			}
			break;

			case TIMES: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTTimes times = new ASTTimes(tok, lhs, rhs);
				stOperand.push(times);
			}
			break;

			case DIV: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTDivision div = new ASTDivision(tok, lhs, rhs);
				stOperand.push(div);
			}
			break;

			case MODULUS: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTModulus modulus = new ASTModulus(tok, lhs, rhs);
				stOperand.push(modulus);
			}
			break;

			case DOT: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTCallFunctionExpr cm = (ASTCallFunctionExpr) rhs;
				// cm.setInstanceName(lhs);
				stOperand.push(cm);
			}
			break;

			case LEFTBRACKET: {
				ASTExpression indexExpr = stOperand.pop();
				ASTExpression arrayExpr = stOperand.pop();
				ASTArrayIndexExpr indexArray = new ASTArrayIndexExpr(arrayExpr.getToken(), arrayExpr, indexExpr);
				stOperand.push(indexArray);
			}
			break;

			default: {
				System.err.println("parseBinary(): Error in parsing");
			}
		}
	}

	public boolean isBinary(Tokens operator)
	{
		switch (operator)
		{
			case OR:
			case AND:
			case EQUALS:
			case NOTEQUALS:
			case LESSTHAN:
			case LESSTHANOREQUAL:
			case GREATERTHAN:
			case GREATERTHANOREQUAL:
			case PLUS:
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

			default: {
				return false;
			}
		}
	}

	public int getPriority(Tokens operator)
	{
		switch (operator)
		{

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

			case GREATERTHAN: {
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

			default: {
				return 0;
			}
		}
	}

	public void parseTerm1() throws ParseException
	{
		switch (token.getToken())
		{
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

			case NOTEQUALS: {
				pushOperator(token);
				eat(Tokens.NOTEQUALS);
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
				ASTExpression indexExpr = parseExpression();
				eat(Tokens.RIGHTBRACKET);
				stOperator.pop();
				stOperand.push(indexExpr);
				parseTerm1();
			}
			break;

			case RIGHTPAREN:
			case SEMICOLON:
			case COMMA:
			case RIGHTBRACKET:
			case RIGHTBRACE: {
				// Epsilon expected
			}
			break;

			default: {
				throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
			}
		}
	}

	public Token checkNotNull(Token token) throws ParseException
	{
		if (token == null) {
			throw new ParseException(0, 0, "Token is null, cannot perform operation.");
		}

		return token;
	}

	public Token peek() throws ParseException
	{
		Token tok = null;

		if (checkNotNull(token) != null) {
			tok = checkNotNull(lexer.peekToken());
		}

		return tok;
	}

	public void advance()
	{
		try {
			token = lexer.nextToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void eat(Tokens tok) throws ParseException
	{
		if (tok == checkNotNull(token).getToken()) {
			advance();
		} else {
			error(checkNotNull(token).getToken(), tok);
		}
	}

	private boolean matchAndConsume(Tokens expected)
	{
		if (token.getToken() == expected) {
			advance();
			return true;
		}
		return false;
	}

	private void error(Tokens found, Tokens expected) throws ParseException
	{
		throw new ParseException(token.getRow(), token.getCol(),
				"Invalid token : " + found + " Expected token : " + expected);
	}
}
