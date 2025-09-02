package knight.compiler.semantics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import knight.compiler.ast.controlflow.ASTConditionalBranch;
import knight.compiler.ast.controlflow.ASTIfChain;
import knight.compiler.ast.controlflow.ASTWhile;
import knight.compiler.ast.expressions.ASTArrayIndexExpr;
import knight.compiler.ast.expressions.ASTArrayLiteral;
import knight.compiler.ast.expressions.ASTIdentifierExpr;
import knight.compiler.ast.expressions.ASTIntLiteral;
import knight.compiler.ast.expressions.ASTLambda;
import knight.compiler.ast.expressions.ASTPlus;
import knight.compiler.ast.expressions.ASTTimes;
import knight.compiler.ast.program.ASTArgument;
import knight.compiler.ast.program.ASTClass;
import knight.compiler.ast.program.ASTFunction;
import knight.compiler.ast.program.ASTIdentifier;
import knight.compiler.ast.program.ASTInterface;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.ast.program.ASTProperty;
import knight.compiler.ast.program.ASTVariable;
import knight.compiler.ast.program.ASTVariableInit;
import knight.compiler.ast.statements.ASTBody;
import knight.compiler.ast.statements.ASTCallFunctionStat;
import knight.compiler.ast.statements.ASTFieldAssign;
import knight.compiler.ast.statements.ASTReturnStatement;
import knight.compiler.ast.types.ASTFunctionType;
import knight.compiler.ast.types.ASTIntArrayType;
import knight.compiler.ast.types.ASTIntType;
import knight.compiler.ast.types.ASTVoidType;
import knight.compiler.lexer.Token;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.model.SymbolClass;
import knight.compiler.semantics.model.SymbolFunction;
import knight.compiler.semantics.model.SymbolProgram;

public class BuildSymbolTreeTest
{
	private BuildSymbolTree symbolTree;
	private Token dummyToken;

	@BeforeEach
	public void setUp()
	{
		DiagnosticReporter.clear();
		symbolTree = new BuildSymbolTree();
		dummyToken = new Token(null, 0, 0);
	}

	private ASTIdentifier createIdentifier(String name)
	{
		return new ASTIdentifier(dummyToken, name);
	}

	private ASTIdentifierExpr createIdentifierExpr(String name)
	{
		return new ASTIdentifierExpr(dummyToken, name);
	}

	private ASTIntLiteral createIntLiteral(int value)
	{
		return new ASTIntLiteral(dummyToken, value);
	}

	@Test
	public void visit_program_with_global_function_should_add_to_symbol_table()
	{
		ASTFunction function = new ASTFunction(dummyToken, new ASTIntType(dummyToken), createIdentifier("testFunc"),
				Collections.emptyList(), new ASTBody(dummyToken, Collections.emptyList()), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);

		SymbolProgram symbolProgram = symbolTree.getSymbolProgram();
		assertNotNull(symbolProgram.getGlobalFunction("testFunc"));
		assertEquals("int", symbolProgram.getGlobalFunction("testFunc").getReturnType().toString());
	}

	@Test
	public void visit_class_with_properties_and_methods_should_build_correct_symbols()
	{
		ASTProperty property = new ASTProperty(dummyToken, new ASTIntType(dummyToken), createIdentifier("count"), null,
				false);

		ASTFunction method = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("increment"),
				Collections.emptyList(), new ASTBody(dummyToken, Collections.emptyList()), false, false);

		ASTClass astClass = new ASTClass(dummyToken, createIdentifier("Counter"), Collections.singletonList(property),
				Collections.singletonList(method), null, Collections.emptyList(), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(astClass));

		symbolTree.visit(program);

		SymbolProgram symbolProgram = symbolTree.getSymbolProgram();
		SymbolClass counterClass = symbolProgram.getClass("Counter");

		assertNotNull(counterClass);
		assertNotNull(counterClass.getProperty("count"));
		assertNotNull(counterClass.getFunction("increment"));
	}

	@Test
	public void visit_duplicate_variables_in_same_scope_should_report_error()
	{
		ASTVariable var1 = new ASTVariable(dummyToken, new ASTIntType(dummyToken), createIdentifier("x"), false);
		ASTVariable var2 = new ASTVariable(dummyToken, new ASTIntType(dummyToken), createIdentifier("x"), false);

		ASTBody functionBody = new ASTBody(dummyToken, Arrays.asList(var1, var2));

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("testScope"),
				Collections.emptyList(), functionBody, false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));
		symbolTree.visit(program);

		assertTrue(DiagnosticReporter.hasErrors());
		assertEquals("0:0: ERROR: Variable x already declared in this scope",
				DiagnosticReporter.getDiagnostics().get(0).toString());
	}

	@Test
	public void visit_variable_in_nested_scopes_should_detect_duplicate_declarations()
	{
		ASTVariable outerVar = new ASTVariable(dummyToken, new ASTIntType(dummyToken), createIdentifier("x"), false);
		ASTVariable innerVar = new ASTVariable(dummyToken, new ASTIntType(dummyToken), createIdentifier("x"), false);
		ASTBody innerBody = new ASTBody(dummyToken, Collections.singletonList(innerVar));
		ASTBody outerBody = new ASTBody(dummyToken, Arrays.asList(outerVar, innerBody));
		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("testScope"),
				Collections.emptyList(), outerBody, false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));
		symbolTree.visit(program);

		assertTrue(DiagnosticReporter.hasErrors());
		assertEquals("0:0: ERROR: Variable x already declared in this scope",
				DiagnosticReporter.getDiagnostics().get(0).toString());
	}

	@Test
	public void visit_function_with_parameters_should_register_parameters()
	{
		ASTArgument param = new ASTArgument(dummyToken, new ASTIntType(dummyToken), createIdentifier("value"));

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("testParams"),
				Collections.singletonList(param), new ASTBody(dummyToken, Collections.emptyList()), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);

		SymbolFunction symbolFunction = symbolTree.getSymbolProgram().getGlobalFunction("testParams");
		assertNotNull(symbolFunction);
		assertEquals(1, symbolFunction.getParameters().size());
		assertEquals("value", symbolFunction.getParameters().get(0).getName());
	}

	@Test
	public void visit_class_with_inheritance_should_set_parent_class_reference()
	{
		ASTClass parentClass = new ASTClass(dummyToken, createIdentifier("Parent"),
				Collections.singletonList(new ASTProperty(dummyToken, new ASTIntType(dummyToken),
						createIdentifier("parentField"), null, false)),
				Collections.emptyList(), null, Collections.emptyList(), false, false);

		ASTClass childClass = new ASTClass(dummyToken, createIdentifier("Child"), Collections.emptyList(),
				Collections.emptyList(), createIdentifier("Parent"), Collections.emptyList(), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(),
				Arrays.asList(parentClass, childClass));

		symbolTree.visit(program);

		SymbolProgram symbolProgram = symbolTree.getSymbolProgram();
		SymbolClass child = symbolProgram.getClass("Child");
		SymbolClass parent = symbolProgram.getClass("Parent");

		assertNotNull(child);
		assertNotNull(parent);
		assertEquals("Parent", child.getParentClassName());
	}

	@Test
	public void visit_class_implementing_interface_should_record_interface_name()
	{
		ASTInterface interfaceAst = new ASTInterface(dummyToken, createIdentifier("Runnable"),
				Collections.singletonList(new ASTFunction(dummyToken, new ASTVoidType(dummyToken),
						createIdentifier("run"), Collections.emptyList(), null, false, false)),
				Collections.emptyList());

		ASTClass classAst = new ASTClass(dummyToken, createIdentifier("Task"), Collections.emptyList(),
				Collections.singletonList(new ASTFunction(dummyToken, new ASTVoidType(dummyToken),
						createIdentifier("run"), Collections.emptyList(),
						new ASTBody(dummyToken, Collections.emptyList()), false, false)),
				null, Collections.singletonList(createIdentifier("Runnable")), false, false);
		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Arrays.asList(interfaceAst, classAst));

		symbolTree.visit(program);

		SymbolProgram symbolProgram = symbolTree.getSymbolProgram();
		SymbolClass taskClass = symbolProgram.getClass("Task");

		assertNotNull(taskClass);
		assertTrue(taskClass.getImplementedInterfaces().contains("Runnable"));
	}

	@Test
	public void visit_duplicate_class_declaration_should_report_error()
	{
		ASTClass class1 = new ASTClass(dummyToken, createIdentifier("Duplicate"), Collections.emptyList(),
				Collections.emptyList(), null, Collections.emptyList(), false, false);
		ASTClass class2 = new ASTClass(dummyToken, createIdentifier("Duplicate"), Collections.emptyList(),
				Collections.emptyList(), null, Collections.emptyList(), false, false);
		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Arrays.asList(class1, class2));

		symbolTree.visit(program);
		assertTrue(DiagnosticReporter.hasErrors());
		assertEquals("0:0: ERROR: Class Duplicate is already defined!",
				DiagnosticReporter.getDiagnostics().get(0).toString());
	}

	@Test
	public void visit_property_outside_class_should_report_error()
	{
		ASTProperty property = new ASTProperty(dummyToken, new ASTIntType(dummyToken), createIdentifier("invalid"),
				null, false);
		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(property));

		symbolTree.visit(program);
		assertTrue(DiagnosticReporter.hasErrors());
		assertEquals("0:0: ERROR: Property declared outside of class",
				DiagnosticReporter.getDiagnostics().get(0).toString());
	}

	@Test
	public void visit_nested_scopes_should_handle_variable_resolution_correctly()
	{
		ASTVariable globalVar = new ASTVariable(dummyToken, new ASTIntType(dummyToken), createIdentifier("global"),
				false);
		ASTArgument param = new ASTArgument(dummyToken, new ASTIntType(dummyToken), createIdentifier("param"));
		ASTVariable localVar = new ASTVariable(dummyToken, new ASTIntType(dummyToken), createIdentifier("local"),
				false);
		ASTBody innerBody = new ASTBody(dummyToken, Collections.singletonList(
				new ASTVariable(dummyToken, new ASTIntType(dummyToken), createIdentifier("inner"), false)));
		ASTBody functionBody = new ASTBody(dummyToken, Arrays.asList(localVar, innerBody));
		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("testNested"),
				Collections.singletonList(param), functionBody, false, false);
		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Arrays.asList(globalVar, function));

		symbolTree.visit(program);

		SymbolProgram symbolProgram = symbolTree.getSymbolProgram();
		assertNotNull(symbolProgram.getGlobalVariable("global"));
		assertNotNull(symbolProgram.getGlobalFunction("testNested"));

		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visit_expression_in_variable_init_should_traverse_all_children()
	{
		ASTPlus addition = new ASTPlus(dummyToken, createIntLiteral(5), createIntLiteral(3));

		ASTVariableInit variable = new ASTVariableInit(dummyToken, new ASTIntType(dummyToken),
				createIdentifier("result"), addition, false);

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("testExpr"),
				Collections.emptyList(), new ASTBody(dummyToken, Collections.singletonList(variable)), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);

		assertFalse(DiagnosticReporter.hasErrors());
		assertNotNull(symbolTree.getSymbolProgram().getGlobalFunction("testExpr"));
	}

	@Test
	public void visit_variable_init_should_register_variable_and_visit_expression()
	{
		ASTIntLiteral initialValue = createIntLiteral(42);
		ASTVariableInit variableInit = new ASTVariableInit(dummyToken, new ASTIntType(dummyToken),
				createIdentifier("initializedVar"), initialValue, false);

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("testInit"),
				Collections.emptyList(), new ASTBody(dummyToken, Collections.singletonList(variableInit)), false,
				false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);

		SymbolProgram symbolProgram = symbolTree.getSymbolProgram();
		assertNotNull(symbolProgram.getGlobalFunction("testInit"));
		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visit_field_assign_should_visit_instance_field_and_value()
	{
		ASTIdentifierExpr instance = createIdentifierExpr("obj");
		ASTIdentifierExpr field = createIdentifierExpr("field");
		ASTIntLiteral value = createIntLiteral(10);

		ASTFieldAssign fieldAssign = new ASTFieldAssign(dummyToken, instance, field, value);

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken),
				createIdentifier("testFieldAssign"), Collections.emptyList(),
				new ASTBody(dummyToken, Collections.singletonList(fieldAssign)), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);
		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visit_array_operations_should_visit_array_and_index()
	{
		ASTIdentifierExpr arrayVar = createIdentifierExpr("arr");
		ASTIntLiteral index = createIntLiteral(0);

		ASTArrayIndexExpr arrayIndex = new ASTArrayIndexExpr(dummyToken, arrayVar, index);

		ASTVariableInit init = new ASTVariableInit(dummyToken, new ASTIntType(dummyToken), createIdentifier("element"),
				arrayIndex, false);

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("testArray"),
				Collections.emptyList(), new ASTBody(dummyToken, Collections.singletonList(init)), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);
		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visit_control_flow_statements_should_visit_condition_and_body()
	{
		ASTIntLiteral condition = createIntLiteral(1);
		ASTBody body = new ASTBody(dummyToken, Collections.emptyList());

		ASTWhile whileLoop = new ASTWhile(dummyToken, condition, body);
		ASTIfChain ifChain = new ASTIfChain(dummyToken,
				Collections.singletonList(new ASTConditionalBranch(dummyToken, condition, body)), null);

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken),
				createIdentifier("testControlFlow"), Collections.emptyList(),
				new ASTBody(dummyToken, Arrays.asList(whileLoop, ifChain)), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);
		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visit_function_call_should_visit_all_arguments()
	{
		ASTCallFunctionStat functionCall = new ASTCallFunctionStat(dummyToken, createIdentifierExpr("print"), null,
				Arrays.asList(createIntLiteral(1), createIntLiteral(2)));

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("testCalls"),
				Collections.emptyList(), new ASTBody(dummyToken, Collections.singletonList(functionCall)), false,
				false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);
		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visit_lambda_should_create_function_scope()
	{
		ASTArgument lambdaArg = new ASTArgument(dummyToken, new ASTIntType(dummyToken), createIdentifier("x"));

		ASTLambda lambda = new ASTLambda(dummyToken, new ASTIntType(dummyToken), Collections.singletonList(lambdaArg),
				new ASTBody(dummyToken, Collections.emptyList()));

		ASTVariableInit init = new ASTVariableInit(dummyToken, new ASTFunctionType(dummyToken),
				createIdentifier("func"), lambda, false);

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken), createIdentifier("testLambda"),
				Collections.emptyList(), new ASTBody(dummyToken, Collections.singletonList(init)), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);
		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visit_array_literal_should_visit_all_elements()
	{
		ASTArrayLiteral arrayLiteral = new ASTArrayLiteral(dummyToken,
				Arrays.asList(createIntLiteral(1), createIntLiteral(2), createIntLiteral(3)));

		ASTVariableInit init = new ASTVariableInit(dummyToken, new ASTIntArrayType(dummyToken),
				createIdentifier("numbers"), arrayLiteral, false);

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken),
				createIdentifier("testArrayLiteral"), Collections.emptyList(),
				new ASTBody(dummyToken, Collections.singletonList(init)), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);
		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visit_binary_operations_should_visit_both_operands()
	{
		ASTPlus addition = new ASTPlus(dummyToken, createIntLiteral(5), createIntLiteral(3));

		ASTTimes multiplication = new ASTTimes(dummyToken, addition, createIntLiteral(2));

		ASTVariableInit init = new ASTVariableInit(dummyToken, new ASTIntType(dummyToken), createIdentifier("result"),
				multiplication, false);

		ASTFunction function = new ASTFunction(dummyToken, new ASTVoidType(dummyToken),
				createIdentifier("testBinaryOps"), Collections.emptyList(),
				new ASTBody(dummyToken, Collections.singletonList(init)), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);
		assertFalse(DiagnosticReporter.hasErrors());
	}

	@Test
	public void visit_return_statement_with_expression_should_visit_expression()
	{
		ASTReturnStatement returnStmt = new ASTReturnStatement(dummyToken, createIntLiteral(42));

		ASTFunction function = new ASTFunction(dummyToken, new ASTIntType(dummyToken), createIdentifier("testReturn"),
				Collections.emptyList(), new ASTBody(dummyToken, Collections.singletonList(returnStmt)), false, false);

		ASTProgram program = new ASTProgram(dummyToken, Collections.emptyList(), Collections.singletonList(function));

		symbolTree.visit(program);
		assertFalse(DiagnosticReporter.hasErrors());
	}
}
