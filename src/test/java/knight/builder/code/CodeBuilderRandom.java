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

package knight.builder.code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.lang.reflect.Constructor;
import com.github.javafaker.Faker;

import knight.builder.code.declarations.*;
import knight.builder.code.expressions.*;
import knight.builder.code.expressions.operations.*;
import knight.builder.code.statements.*;
import knight.builder.code.types.*;

/*
 * File: CodeBuilderRandom.java
 * @author: Mart van der Zalm
 * Date: 2024-03-26
 * Description:
 */
public class CodeBuilderRandom
{
	private final List<Class<? extends CodeBuilderStatement>> statements = new ArrayList<>();
	private final List<Class<? extends CodeBuilderExpression>> expressions = new ArrayList<>();
	private final List<Class<? extends CodeBuilderExpression>> conditions = new ArrayList<>();
	private final List<Class<? extends CodeBuilderType>> types = new ArrayList<>();
	private final List<Class<? extends CodeBuilderStatement>> body = new ArrayList<>();

	private final Random random;
	private final Faker faker;

	private final String[] tokens = { "int", "string", "bool", "true", "false", "public", "protected", "private",
			"class", "new", "include", "fn", "ext", "use", "asm", "if", "else", "while", "for", "ret", "void" };

	public CodeBuilderRandom()
	{
		this.random = new Random();
		this.faker = new Faker();

		// Statements
		statements.add(CodeBuilderAssign.class);
		// statements.add(CodeBuilderWhile.class);
		// statements.add(CodeBuilderForLoop.class);
		// statements.add(CodeBuilderIfThenElse.class);

		// Expressions
		expressions.add(CodeBuilderIntLiteral.class);
		expressions.add(CodeBuilderStringLiteral.class);
		expressions.add(CodeBuilderBooleanLiteral.class);
		expressions.add(CodeBuilderIdentifierExpr.class);

		// Conditions
		conditions.add(CodeBuilderLessThan.class);
		conditions.add(CodeBuilderLessThanOrEqual.class);
		conditions.add(CodeBuilderGreaterThan.class);
		conditions.add(CodeBuilderGreaterThanOrEqual.class);
		conditions.add(CodeBuilderEquals.class);

		// Types
		types.add(CodeBuilderIntType.class);
		// types.add(CodeBuilderStringType.class);
		types.add(CodeBuilderBooleanType.class);
		types.add(CodeBuilderIdentifierType.class);

		// Body
		body.add(CodeBuilderAssign.class);
	}

	public String className()
	{
		String input = this.identifier();

		return String.join("", (input.substring(0, 1).toUpperCase() + input.substring(1)).split("\\s+"));
	}

	public String identifier()
	{
		String id;

		do {
			id = this.faker.lorem().word();
		} while (isToken(id));

		return id;
	}

	public int integer()
	{
		int min = 1;
		int max = 10000;

		return this.random.nextInt(max - min + 1) + min;
	}

	public String string()
	{
		String str;

		do {
			str = this.faker.lorem().sentence();
		} while (isToken(str));

		return str;
	}

	public boolean bool()
	{
		return faker.bool().bool();
	}

	public CodeBuilderStatement statement()
	{
		try {
			Constructor<? extends CodeBuilderStatement> constructor = statements.get(random.nextInt(statements.size()))
					.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public CodeBuilderExpression expression()
	{
		try {
			Constructor<? extends CodeBuilderExpression> constructor = expressions
					.get(random.nextInt(expressions.size())).getDeclaredConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public CodeBuilderExpression condition()
	{
		try {
			Constructor<? extends CodeBuilderExpression> constructor = conditions.get(random.nextInt(conditions.size()))
					.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public CodeBuilderType type()
	{
		try {
			Constructor<? extends CodeBuilderType> constructor = types.get(random.nextInt(types.size()))
					.getDeclaredConstructor();
			return constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean isToken(String word)
	{
		for (String token : tokens) {
			if (token.equals(word)) {
				return true;
			}
		}
		return false;
	}
}
