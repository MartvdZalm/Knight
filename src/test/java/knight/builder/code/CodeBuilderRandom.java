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
import java.util.List;
import java.util.Random;
import java.lang.reflect.Constructor;
import com.github.javafaker.Faker;

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
    
    private final Random random;
    private final Faker faker;

    public CodeBuilderRandom()
    {
        this.random = new Random();
        this.faker = new Faker();

        // Statements
        statements.add(CodeBuilderAssign.class);

        // Expressions
        expressions.add(CodeBuilderIntLiteral.class);
        expressions.add(CodeBuilderStringLiteral.class);
    }

    public String identifier()
    {
        return this.faker.lorem().word();
    }

    public int integer()
    {
        return this.random.nextInt(0, 100000);
    }

    public CodeBuilderStatement statement()
    {        
        try {
        	Constructor<? extends CodeBuilderStatement> constructor = statements.get(random.nextInt(statements.size())).getDeclaredConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
          	e.printStackTrace();
          	return null;
        }
    }

    public CodeBuilderExpression expression()
    {
    	try {
    		Constructor<? extends CodeBuilderExpression> constructor = expressions.get(random.nextInt(expressions.size())).getDeclaredConstructor();
            return constructor.newInstance();
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
}
