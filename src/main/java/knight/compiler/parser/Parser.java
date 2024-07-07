/*
 * MIT License
 * 
 * Copyright (c) 2023, Mart van der Zalm
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package knight.compiler.parser;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import knight.compiler.ast.declarations.*;
import knight.compiler.ast.expressions.*;
import knight.compiler.ast.expressions.operations.*;
import knight.compiler.ast.statements.*;
import knight.compiler.ast.statements.conditionals.*;
import knight.compiler.ast.types.*;
import knight.compiler.ast.*;

import knight.compiler.lexer.Lexer;
import knight.compiler.lexer.Symbol;
import knight.compiler.lexer.Token;
import knight.compiler.lexer.Tokens;

/*
 * File: Parser.java
 * @author: Mart van der Zalm
 * Date: 2024-01-06
 * Description:
 */
public class Parser
{
    public Lexer lexer;
    public Token token;
    private static final Token SENTINEL = new Token(Symbol.symbol("SENTINEL", Tokens.SENTINEL), 0, 0);
    private Deque<Token> stOperator = new ArrayDeque<>();
    private Deque<ASTExpression> stOperand = new ArrayDeque<>();

    public Parser(BufferedReader bufferedReader)
    {
        lexer = new Lexer(bufferedReader);
        stOperator.push(SENTINEL);
    }

    public AST parse() throws ParseException
    {
        ASTProgram program = null;

        try {
			List<ASTClass> classList = new ArrayList<>();
			List<ASTFunction> functionList = new ArrayList<>();
			List<ASTVariable> variableList = new ArrayList<>();
			List<ASTInlineASM> inlineASMList = new ArrayList<>();

			token = lexer.nextToken();

			do {
				switch (token.getToken()) {

					case CLASS: {
						classList.add(parseClass());
					} break;

					case FUNCTION: {
						functionList.add(parseFunction());
					} break;

					case ASM: {
						inlineASMList.add(parseInlineASM());
					} break;

					case INTEGER:
					case STRING:
					case BOOLEAN:
					case IDENTIFIER: {
						variableList.add(parseVariable());
					} break;
					
					default: {
						throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
					}
                }
                
            } while (token != null);
			
            program = new ASTProgram(token, classList, functionList, variableList, inlineASMList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return program;
    }

    public ASTInlineASM parseInlineASM() throws ParseException
    {
    	List<String> lines = new ArrayList<>();
    	eat(Tokens.ASM);
    	eat(Tokens.LEFTBRACE);
    	while (token.getToken() == Tokens.STRING) {
    		lines.add(token.getSymbol());
    		eat(Tokens.STRING);
    	}
    	eat(Tokens.RIGHTBRACE);

    	return new ASTInlineASM(token, lines);
    }

	public ASTClass parseClass() throws ParseException
	{
		List<ASTFunction> functions = new ArrayList<>();
		List<ASTVariable> variables = new ArrayList<>();

		eat(Tokens.CLASS);
		ASTIdentifier id = parseIdentifier();
		eat(Tokens.LEFTBRACE);

		while (token.getToken() != Tokens.RIGHTBRACE) {
			if (token.getToken() == Tokens.FUNCTION) {
				functions.add(parseFunction());
			} else {
				variables.add(parseVariable());
			}
		}
		eat(Tokens.RIGHTBRACE);
		return new ASTClass(id.getToken(), id, functions, variables);
	}

	public ASTFunction parseFunction() throws ParseException
	{		
		List<ASTVariable> variables = new ArrayList<>();
		List<ASTStatement> statements = new ArrayList<>();
		List<ASTInlineASM> inlineASM = new ArrayList<>();

		eat(Tokens.FUNCTION);
		ASTIdentifier id = parseIdentifier();
		List<ASTArgument> argumentList = parseArguments();
		eat(Tokens.COLON);
		ASTType returnType = parseType();
		eat(Tokens.LEFTBRACE);

		while (token.getToken() != Tokens.RIGHTBRACE && token.getToken() != Tokens.RETURN) {

			switch (token.getToken()) {
			    case WHILE:
			    // case FOR:
			    case IF: {
			    	statements.add(parseStatement());
			    } break;

			    case INTEGER:
			    case STRING:
			    case BOOLEAN: {
			    	variables.add(parseVariable());
			    } break;

			    case ASM: {
					inlineASM.add(parseInlineASM());
			    } break;

			    case IDENTIFIER: {
			    	if (peek().getToken() != Tokens.ASSIGN && peek().getToken() != Tokens.LEFTPAREN) { // Then it is a variable
			    		variables.add(parseVariable());
			    	} else {
			    		statements.add(parseStatement());
			    	}
			    } break;

			   	case THIS: {
			   		ASTThis pointer = new ASTThis(token);
			   		eat(Tokens.THIS);
			   		eat(Tokens.ARROW);
			   		ASTIdentifier variable = parseIdentifier();
			   		eat(Tokens.ASSIGN);
			   		ASTExpression expression = parseExpression();

			   		statements.add(new ASTPointerAssign(token, pointer, variable, expression));
			   	} break;

			    default: {
					throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
				}
			}
		}

		ASTExpression returnExpr = null;
		if (token.getToken() == Tokens.RETURN) {
			eat(Tokens.RETURN);
			returnExpr = parseExpression();
		}
		eat(Tokens.RIGHTBRACE);

		if (returnExpr != null) {
			return new ASTFunctionReturn(token, returnType, id, argumentList, variables, statements, inlineASM, returnExpr);
		}

		return new ASTFunction(token, returnType, id, argumentList, variables, statements, inlineASM);
	}

	public ASTVariable parseVariable() throws ParseException
	{
		ASTType type = parseType();
		ASTIdentifier id = parseIdentifier();

		ASTVariable variable = null;
		if (checkNotNull(token).getToken() == Tokens.SEMICOLON) {
			variable = new ASTVariable(token, type, id);
			eat(Tokens.SEMICOLON);
		} else {
			eat(Tokens.ASSIGN);
			variable = new ASTVariableInit(token, type, id, parseExpression());
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
		ASTIdentifier argumentId = null;
		ASTType argumentType = parseType();
		if (token.getToken() == Tokens.IDENTIFIER) {
			argumentId = new ASTIdentifier(token, token.getSymbol());
		}
		eat(Tokens.IDENTIFIER);

		return new ASTArgument(argumentId.getToken(), argumentType, argumentId);
	}

	public ASTStatement parseStatement() throws ParseException
    {
		switch (token.getToken()) {

			case LEFTBRACE: {
				Token tok = token;
				eat(Tokens.LEFTBRACE);
				List<ASTStatement> body = new ArrayList<>();
				while (token.getToken() != Tokens.RIGHTBRACE) {
					ASTStatement stat = parseStatement();
					body.add(stat);
				}
				eat(Tokens.RIGHTBRACE);
				ASTBlock block = new ASTBlock(tok, body);
				return block;
			}
	
			case IF: {
				Token tok = token;
				eat(Tokens.IF);
				eat(Tokens.LEFTPAREN);
				ASTExpression expr = parseExpression();
				eat(Tokens.RIGHTPAREN);
				ASTStatement then = parseStatement();
				ASTStatement elze = new ASTSkip(then.getToken()); 
				if (token.getToken() == Tokens.ELSE) {
					eat(Tokens.ELSE);
					elze = parseStatement();
				}
				ASTIfThenElse result = new ASTIfThenElse(tok, expr, then, elze);
				return result;
			}
	
			case WHILE: {
				Token tok = token;
				eat(Tokens.WHILE);
				eat(Tokens.LEFTPAREN);
				ASTExpression expr = parseExpression();
				eat(Tokens.RIGHTPAREN);
				ASTStatement body = parseStatement();
				ASTWhile result = new ASTWhile(tok, expr, body);
				return result;
			}

			// case FOR: {
			// 	Token tok = token;
			// 	eat(Tokens.FOR);
			// 	eat(Tokens.LEFTPAREN);
			// 	ASTVariable initialization = parseVariable();
			// 	ASTExpression condition = parseExpression();
			// 	ASTExpression update = parseExpression();
			// 	eat(Tokens.RIGHTPAREN);
			// 	ASTStatement body = parseStatement();
			// 	ASTForLoop forLoop = new ASTForLoop(tok, initialization, condition, update, body);
			// 	return forLoop;
			// }

			case RETURN: {
				Token tok = token;
				eat(Tokens.RETURN);
				ASTExpression returnExpr = parseExpression();
				ASTReturnStatement returnStatement = new ASTReturnStatement(tok, returnExpr);
				return returnStatement;
			}

			case IDENTIFIER: {
				ASTIdentifier id = parseIdentifier();

				switch (token.getToken()) {
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
		switch (token.getToken()) {

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
				ASTStringType st = new ASTStringType(token);
				eat(Tokens.STRING);
				return st;
			}

			case BOOLEAN: {
				ASTBooleanType bt = new ASTBooleanType(token);
				eat(Tokens.BOOLEAN);
				return bt;
			}

			case IDENTIFIER: {
				ASTIdentifierType id = new ASTIdentifierType(token, token.getSymbol());
				eat(Tokens.IDENTIFIER);
				return id;
			}

			case VOID: {
				ASTVoidType vt = new ASTVoidType(token);
				eat(Tokens.VOID);
				return vt;
			}

			default: {
				throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
			}
		}
	}

	public void parseExpr() throws ParseException
	{
		switch (token.getToken()) {

			case INTEGER: {
				ASTIntLiteral lit = new ASTIntLiteral(token, Integer.parseInt(token.getSymbol()));
				eat(Tokens.INTEGER);
				stOperand.push(lit);
				parseTerm1();
			} break;

			case STRING: {
				ASTStringLiteral sl = new ASTStringLiteral(token, (String) token.getSymbol());
				eat(Tokens.STRING);
				stOperand.push(sl);
				parseTerm1();
			} break;

			case TRUE: {
				ASTTrue true1 = new ASTTrue(token);
				eat(Tokens.TRUE);
				stOperand.push(true1);
				parseTerm1();
			} break;

			case FALSE: {
				ASTFalse false1 = new ASTFalse(token);
				eat(Tokens.FALSE);
				stOperand.push(false1);
				parseTerm1();
			} break;

			case IDENTIFIER: {
				ASTIdentifierExpr id = new ASTIdentifierExpr(token, (String) token.getSymbol());
				eat(Tokens.IDENTIFIER);
				if (token.getToken() == Tokens.LEFTPAREN) {
					ASTExpression expr = parseCallFunction(id);
					stOperand.push(expr);	
				} else {
					stOperand.push(id);
				}
				parseTerm1();
			} break;

			case NEW: {
				pushOperator(token);
				eat(Tokens.NEW);

				switch (token.getToken()) {
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
					} break;

					case IDENTIFIER: {
						ASTIdentifierExpr idExpr = new ASTIdentifierExpr(token, token.getSymbol());
						eat(Tokens.IDENTIFIER);
						eat(Tokens.LEFTPAREN);
						eat(Tokens.RIGHTPAREN);
						stOperand.push(idExpr);
					} break;

					default: {
						throw new ParseException(token.getRow(), token.getCol(), "Invalid token :" + token.getToken());
					}
				}

				parseTerm1();
			} break;

			default: {
				throw new ParseException(token.getRow(), token.getCol(), "Invalid token : " + token.getToken());
			}
		}
	}

	public ASTExpression parseCallFunction(ASTIdentifierExpr methodId) throws ParseException
	{
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
		return new ASTCallFunctionExpr(tok, null, methodId, exprList);
	}

	public void pushOperator(Token current)
	{
		Token top = stOperator.peek();

		while (getPriority(top.getToken()) >= getPriority(current.getToken())) {
			popOperator();
			top = stOperator.peek();
		}

		stOperator.push(current);
	}

	public void popOperator()
	{
		Token top = stOperator.pop();

		if (isBinary(top.getToken())) {
			parseBinary(top);
		} else {
			parseUnary(top);
		}
	}

	public void parseUnary(Token tok)
	{
		switch (tok.getToken()) {

			case NEW: {
				ASTExpression expr = stOperand.pop();
				ASTIdentifierExpr idExpr = (ASTIdentifierExpr) expr;
				ASTNewInstance instance = new ASTNewInstance(tok, idExpr);
				stOperand.push(instance);
			} break;

			default: {
				System.err.println("parseUnary(): Error in parsing " + token.getToken() + " " + token.getRow());
			}
		}
	}

	public void parseBinary(Token tok)
	{
		switch (tok.getToken()) {

			case OR: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTOr or = new ASTOr(tok, lhs, rhs);
				stOperand.push(or);
			} break;

			case AND: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTAnd and = new ASTAnd(tok, lhs, rhs);
				stOperand.push(and);
			} break;

			case EQUALS: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTEquals equals = new ASTEquals(tok, lhs, rhs);
				stOperand.push(equals);
			} break;

			case LESSTHAN: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTLessThan lessThan = new ASTLessThan(tok, lhs, rhs);
				stOperand.push(lessThan);
			} break;

			case LESSTHANOREQUAL: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTLessThanOrEqual lessThanOrEqual = new ASTLessThanOrEqual(tok, lhs, rhs);
				stOperand.push(lessThanOrEqual);
			} break;

			case GREATERTHAN: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTGreaterThan greaterThan = new ASTGreaterThan(tok, lhs, rhs);
				stOperand.push(greaterThan);
			} break;

			case GREATERTHANOREQUAL: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTGreaterThanOrEqual greaterThanOrEqual = new ASTGreaterThanOrEqual(tok, lhs, rhs);
				stOperand.push(greaterThanOrEqual);
			} break;
			
			case PLUS: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTPlus plus = new ASTPlus(tok, lhs, rhs);
				stOperand.push(plus);
			} break;

			case MINUS: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTMinus minus = new ASTMinus(tok, lhs, rhs);
				stOperand.push(minus);
			} break;

			case TIMES: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTTimes times = new ASTTimes(tok, lhs, rhs);
				stOperand.push(times);
			} break;

			case DIV: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTDivision div = new ASTDivision(tok, lhs, rhs);
				stOperand.push(div);
			} break;

			case MODULUS: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTModulus modulus = new ASTModulus(tok, lhs, rhs);
				stOperand.push(modulus);
			} break;

			case DOT: {
				ASTExpression rhs = stOperand.pop();
				ASTExpression lhs = stOperand.pop();
				ASTCallFunctionExpr cm = (ASTCallFunctionExpr) rhs;
				cm.setInstanceName(lhs);
				stOperand.push(cm);
			} break;

			case LEFTBRACKET: {
				ASTExpression indexExpr = stOperand.pop();
				ASTExpression arrayExpr = stOperand.pop();
				ASTArrayIndexExpr indexArray = new ASTArrayIndexExpr(arrayExpr.getToken(), arrayExpr, indexExpr);
				stOperand.push(indexArray);
			} break;

			default: {
				System.err.println("parseBinary(): Error in parsing");
			}
		}
	}

	public boolean isBinary(Tokens operator)
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

			default: {
				return 0;
			}
		}
	}

	public void parseTerm1() throws ParseException
	{
		switch (token.getToken()) {

			case AND: {
				pushOperator(token);
				eat(Tokens.AND);
				parseExpr();
				parseTerm1();
			} break;

			case OR: {
				pushOperator(token);
				eat(Tokens.OR);
				parseExpr();
				parseTerm1();
			} break;

			case EQUALS: {
				pushOperator(token);
				eat(Tokens.EQUALS);
				parseExpr();
				parseTerm1();
			} break;

			case LESSTHAN: {
				pushOperator(token);
				eat(Tokens.LESSTHAN);
				parseExpr();
				parseTerm1();
			} break;

			case LESSTHANOREQUAL: {
				pushOperator(token);
				eat(Tokens.LESSTHANOREQUAL);
				parseExpr();
				parseTerm1();
			} break;

			case GREATERTHAN: {
				pushOperator(token);
				eat(Tokens.GREATERTHAN);
				parseExpr();
				parseTerm1();
			} break;

			case GREATERTHANOREQUAL: {
				pushOperator(token);
				eat(Tokens.GREATERTHANOREQUAL);
				parseExpr();
				parseTerm1();
			} break;

			case PLUS: {
				pushOperator(token);
				eat(Tokens.PLUS);
				parseExpr();
				parseTerm1();
			} break;

			case MINUS: {
				pushOperator(token);
				eat(Tokens.MINUS);
				parseExpr();
				parseTerm1();
			} break;

			case TIMES: {
				pushOperator(token);
				eat(Tokens.TIMES);
				parseExpr();
				parseTerm1();
			} break;

			case DIV: {
				pushOperator(token);
				eat(Tokens.DIV);
				parseExpr();
				parseTerm1();
			} break;

			case MODULUS: {
				pushOperator(token);
				eat(Tokens.MODULUS);
				parseExpr();
				parseTerm1();
			} break;

			case LEFTBRACKET: {
				pushOperator(token);
				eat(Tokens.LEFTBRACKET);
				stOperator.push(SENTINEL);
				ASTExpression indexExpr = parseExpression();
				eat(Tokens.RIGHTBRACKET);
				stOperator.pop(); 
				stOperand.push(indexExpr);
				parseTerm1();
			} break;

			case RIGHTPAREN:
			case SEMICOLON:
			case COMMA:
			case RIGHTBRACKET: {
				// Epsilon expected
			} break;

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

    private void error(Tokens found, Tokens expected) throws ParseException
    {
        throw new ParseException(token.getRow(), token.getCol(), "Invalid token : " + found + " Expected token : " + expected);
    }
}