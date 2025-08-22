package knight.compiler.ast.program;

import java.util.List;

import knight.compiler.ast.*;
import knight.compiler.ast.utils.ASTList;
import knight.compiler.lexer.Token;

public class ASTClass extends AST
{
	private ASTIdentifier identifier;
	private ASTList<ASTFunction> functions;
	private ASTList<ASTProperty> properties;
	private ASTIdentifier extendsClass;
	private ASTList<ASTIdentifier> implementsInterfaces;
	private boolean isAbstract;
	private boolean isStatic;

	public ASTClass(Token token, ASTIdentifier identifier, List<ASTProperty> properties, List<ASTFunction> functions,
			ASTIdentifier extendsClass, List<ASTIdentifier> implementsInterfaces, boolean isAbstract, boolean isStatic)
	{
		super(token);

		this.identifier = identifier;
		this.functions = new ASTList<>(functions);
		this.properties = new ASTList<>(properties);
		this.extendsClass = extendsClass;
		this.implementsInterfaces = new ASTList<>(implementsInterfaces);
		this.isAbstract = isAbstract;
		this.isStatic = isStatic;
	}

	public ASTIdentifier getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(ASTIdentifier identifier)
	{
		this.identifier = identifier;
	}

	public List<ASTFunction> getFunctions()
	{
		return functions.getList();
	}

	public int getFunctionCount()
	{
		return functions.getSize();
	}

	public ASTFunction getFunction(int index)
	{
		return functions.getAt(index);
	}

	public void setFunctions(List<ASTFunction> functions)
	{
		this.functions.setList(functions);
	}

	public List<ASTProperty> getProperties()
	{
		return properties.getList();
	}

	public int getPropertyCount()
	{
		return properties.getSize();
	}

	public ASTProperty getProperty(int index)
	{
		return properties.getAt(index);
	}

	public void setProperties(List<ASTProperty> properties)
	{
		this.properties.setList(properties);
	}

	public ASTIdentifier getExtendsClass()
	{
		return extendsClass;
	}

	public List<ASTIdentifier> getImplementsInterfaces()
	{
		return implementsInterfaces.getList();
	}

	public ASTIdentifier getImplementedInterface(int index)
	{
		return implementsInterfaces.getAt(index);
	}

	public int getImplementedInterfaceCount()
	{
		return implementsInterfaces.getSize();
	}

	public boolean isAbstract()
	{
		return isAbstract;
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
