package knight.compiler.optimizations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.ASTAnd;
import knight.compiler.ast.expressions.ASTDivision;
import knight.compiler.ast.expressions.ASTEquals;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.expressions.ASTFalse;
import knight.compiler.ast.expressions.ASTGreaterThan;
import knight.compiler.ast.expressions.ASTIntLiteral;
import knight.compiler.ast.expressions.ASTLessThan;
import knight.compiler.ast.expressions.ASTMinus;
import knight.compiler.ast.expressions.ASTModulus;
import knight.compiler.ast.expressions.ASTNotEquals;
import knight.compiler.ast.expressions.ASTOr;
import knight.compiler.ast.expressions.ASTPlus;
import knight.compiler.ast.expressions.ASTStringLiteral;
import knight.compiler.ast.expressions.ASTTimes;
import knight.compiler.ast.expressions.ASTTrue;
import knight.compiler.ast.program.ASTIdentifier;
import knight.compiler.ast.program.ASTVariableInit;
import knight.compiler.ast.statements.ASTBody;
import knight.compiler.ast.statements.ASTReturnStatement;
import knight.compiler.ast.types.ASTBooleanType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTStringType;
import knight.compiler.lexer.Token;

public class ConstantFoldingTest
{
	private ConstantFolding optimizer;
	private Token dummyToken;

	@BeforeEach
	public void setUp()
	{
		optimizer = new ConstantFolding();
		dummyToken = new Token(null, 0, 0);
	}

	private ASTIntLiteral createIntLiteral(int value)
	{
		ASTIntLiteral lit = new ASTIntLiteral(dummyToken, value);
		lit.setType(new ASTIntType(dummyToken));
		return lit;
	}

	private ASTStringLiteral createStringLiteral(String value)
	{
		ASTStringLiteral lit = new ASTStringLiteral(dummyToken, value);
		lit.setType(new ASTStringType(dummyToken));
		return lit;
	}

	private ASTTrue createTrue()
	{
		ASTTrue lit = new ASTTrue(dummyToken);
		lit.setType(new ASTBooleanType(dummyToken));
		return lit;
	}

	private ASTFalse createFalse()
	{
		ASTFalse lit = new ASTFalse(dummyToken);
		lit.setType(new ASTBooleanType(dummyToken));
		return lit;
	}

	@Test
	public void arithmetic_operations_should_fold_constants()
	{
		ASTPlus plus = new ASTPlus(dummyToken, createIntLiteral(5), createIntLiteral(3));
		plus.setType(new ASTIntType(dummyToken));
		ASTExpression result = optimizer.visit(plus);
		assertInstanceOf(ASTIntLiteral.class, result);
		assertEquals(8, ((ASTIntLiteral) result).getValue());

		ASTMinus minus = new ASTMinus(dummyToken, createIntLiteral(10), createIntLiteral(4));
		minus.setType(new ASTIntType(dummyToken));
		result = optimizer.visit(minus);
		assertInstanceOf(ASTIntLiteral.class, result);
		assertEquals(6, ((ASTIntLiteral) result).getValue());

		ASTTimes times = new ASTTimes(dummyToken, createIntLiteral(6), createIntLiteral(7));
		times.setType(new ASTIntType(dummyToken));
		result = optimizer.visit(times);
		assertInstanceOf(ASTIntLiteral.class, result);
		assertEquals(42, ((ASTIntLiteral) result).getValue());

		ASTDivision div = new ASTDivision(dummyToken, createIntLiteral(20), createIntLiteral(4));
		div.setType(new ASTIntType(dummyToken));
		result = optimizer.visit(div);
		assertInstanceOf(ASTIntLiteral.class, result);
		assertEquals(5, ((ASTIntLiteral) result).getValue());

		ASTModulus mod = new ASTModulus(dummyToken, createIntLiteral(17), createIntLiteral(5));
		mod.setType(new ASTIntType(dummyToken));
		result = optimizer.visit(mod);
		assertInstanceOf(ASTIntLiteral.class, result);
		assertEquals(2, ((ASTIntLiteral) result).getValue());
	}

	@Test
	public void string_concatenation_should_fold_constants()
	{
		ASTPlus concat = new ASTPlus(dummyToken, createStringLiteral("hello"), createStringLiteral(" world"));
		concat.setType(new ASTStringType(dummyToken));
		ASTExpression result = optimizer.visit(concat);

		assertInstanceOf(ASTStringLiteral.class, result);
		assertEquals("hello world", ((ASTStringLiteral) result).getValue());
	}

	@Test
	public void comparison_operations_should_fold_constants()
	{
		ASTEquals equals = new ASTEquals(dummyToken, createIntLiteral(5), createIntLiteral(5));
		equals.setType(new ASTIntType(dummyToken));
		ASTExpression equalsResult = optimizer.visit(equals);
		assertInstanceOf(ASTTrue.class, equalsResult);

		ASTNotEquals notEquals = new ASTNotEquals(dummyToken, createIntLiteral(5), createIntLiteral(3));
		notEquals.setType(new ASTBooleanType(dummyToken));
		ASTExpression notEqualsResult = optimizer.visit(notEquals);
		assertInstanceOf(ASTTrue.class, notEqualsResult);

		ASTLessThan lessThan = new ASTLessThan(dummyToken, createIntLiteral(3), createIntLiteral(5));
		lessThan.setType(new ASTBooleanType(dummyToken));
		ASTExpression lessThanResult = optimizer.visit(lessThan);
		assertInstanceOf(ASTTrue.class, lessThanResult);

		ASTGreaterThan greaterThan = new ASTGreaterThan(dummyToken, createIntLiteral(5), createIntLiteral(3));
		greaterThan.setType(new ASTBooleanType(dummyToken));
		ASTExpression greaterThanResult = optimizer.visit(greaterThan);
		assertInstanceOf(ASTTrue.class, greaterThanResult);
	}

	@Test
	public void logical_operations_should_fold_constants()
	{
		ASTAnd andTrue = new ASTAnd(dummyToken, createTrue(), createTrue());
		andTrue.setType(new ASTBooleanType(dummyToken));
		ASTExpression result = optimizer.visit(andTrue);
		assertInstanceOf(ASTTrue.class, result);

		ASTAnd andFalse = new ASTAnd(dummyToken, createTrue(), createFalse());
		andFalse.setType(new ASTBooleanType(dummyToken));
		result = optimizer.visit(andFalse);
		assertInstanceOf(ASTFalse.class, result);

		ASTOr orTrue = new ASTOr(dummyToken, createTrue(), createFalse());
		orTrue.setType(new ASTBooleanType(dummyToken));
		result = optimizer.visit(orTrue);
		assertInstanceOf(ASTTrue.class, result);

		ASTOr orFalse = new ASTOr(dummyToken, createFalse(), createFalse());
		orFalse.setType(new ASTBooleanType(dummyToken));
		result = optimizer.visit(orFalse);
		assertInstanceOf(ASTFalse.class, result);
	}

	@Test
	public void while_loop_with_false_condition_should_be_removed()
	{
		ASTBody body = new ASTBody(dummyToken, new ArrayList<>(List.of(createIntLiteral(1))));
		ASTWhile whileLoop = new ASTWhile(dummyToken, createFalse(), body);

		optimizer.visit(whileLoop);

		assertTrue(body.getNodes().isEmpty(), "While loop body should be emptied when condition is false");
	}

	@Test
	public void if_chain_should_prune_false_branches()
	{
		ASTBody trueBody = new ASTBody(dummyToken, Collections.singletonList(createIntLiteral(1)));
		ASTBody falseBody = new ASTBody(dummyToken, Collections.singletonList(createIntLiteral(2)));

		ASTConditionalBranch falseBranch = new ASTConditionalBranch(dummyToken, createFalse(), falseBody);
		ASTConditionalBranch trueBranch = new ASTConditionalBranch(dummyToken, createTrue(), trueBody);

		ASTIfChain ifChain = new ASTIfChain(dummyToken, Arrays.asList(falseBranch, trueBranch), null);

		optimizer.visit(ifChain);

		assertEquals(1, ifChain.getBranches().size(), "Should only have one branch after pruning");
		assertSame(trueBody, ifChain.getBranches().get(0).getBody(), "Should keep the true branch");
	}

	@Test
	public void variable_initialization_should_optimize_expression()
	{
		ASTPlus plus = new ASTPlus(dummyToken, createIntLiteral(2), createIntLiteral(3));
		plus.setType(new ASTIntType(dummyToken));

		ASTVariableInit varInit = new ASTVariableInit(dummyToken, new ASTIntType(dummyToken),
				new ASTIdentifier(dummyToken, "x"), plus, false);

		optimizer.visit(varInit);

		assertInstanceOf(ASTIntLiteral.class, varInit.getExpression());
		assertEquals(5, ((ASTIntLiteral) varInit.getExpression()).getValue());
	}

	@Test
	public void return_statement_should_optimize_expression()
	{
		ASTTimes times = new ASTTimes(dummyToken, createIntLiteral(6), createIntLiteral(7));
		times.setType(new ASTIntType(dummyToken));

		ASTReturnStatement returnStmt = new ASTReturnStatement(dummyToken, times);

		optimizer.visit(returnStmt);

		assertInstanceOf(ASTIntLiteral.class, returnStmt.getExpression());
		assertEquals(42, ((ASTIntLiteral) returnStmt.getExpression()).getValue());
	}

	@Test
	public void division_by_zero_should_not_fold()
	{
		ASTDivision div = new ASTDivision(dummyToken, createIntLiteral(5), createIntLiteral(0));
		div.setType(new ASTIntType(dummyToken));

		ASTExpression result = optimizer.visit(div);

		assertInstanceOf(ASTDivision.class, result, "Division by zero should not be folded");
	}

	@Test
	public void modulus_by_zero_should_not_fold()
	{
		ASTModulus mod = new ASTModulus(dummyToken, createIntLiteral(5), createIntLiteral(0));
		mod.setType(new ASTIntType(dummyToken));

		ASTExpression result = optimizer.visit(mod);

		assertInstanceOf(ASTModulus.class, result, "Modulus by zero should not be folded");
	}

	@Test
	public void mixed_type_concatenation_should_work()
	{
		ASTPlus concat1 = new ASTPlus(dummyToken, createStringLiteral("num: "), createIntLiteral(42));
		concat1.setType(new ASTStringType(dummyToken));

		ASTExpression result = optimizer.visit(concat1);
		assertInstanceOf(ASTStringLiteral.class, result);
		assertEquals("num: 42", ((ASTStringLiteral) result).getValue());

		ASTPlus concat2 = new ASTPlus(dummyToken, createIntLiteral(100), createStringLiteral(" people"));
		concat2.setType(new ASTStringType(dummyToken));

		result = optimizer.visit(concat2);
		assertInstanceOf(ASTStringLiteral.class, result);
		assertEquals("100 people", ((ASTStringLiteral) result).getValue());
	}

	@Test
	public void boolean_equality_should_fold()
	{
		ASTEquals eq1 = new ASTEquals(dummyToken, createTrue(), createTrue());
		eq1.setType(new ASTBooleanType(dummyToken));
		ASTExpression result = optimizer.visit(eq1);
		assertInstanceOf(ASTTrue.class, result);

		ASTEquals eq2 = new ASTEquals(dummyToken, createFalse(), createFalse());
		eq2.setType(new ASTBooleanType(dummyToken));
		result = optimizer.visit(eq2);
		assertInstanceOf(ASTTrue.class, result);

		ASTEquals eq3 = new ASTEquals(dummyToken, createTrue(), createFalse());
		eq3.setType(new ASTBooleanType(dummyToken));
		result = optimizer.visit(eq3);
		assertInstanceOf(ASTFalse.class, result);
	}

	@Test
	public void recursive_optimization_should_work()
	{
		ASTPlus inner = new ASTPlus(dummyToken, createIntLiteral(2), createIntLiteral(3));
		inner.setType(new ASTIntType(dummyToken));

		ASTTimes outer = new ASTTimes(dummyToken, inner, createIntLiteral(4));
		outer.setType(new ASTIntType(dummyToken));

		ASTExpression result = optimizer.visit(outer);

		assertInstanceOf(ASTIntLiteral.class, result);
		assertEquals(20, ((ASTIntLiteral) result).getValue());
	}
}
