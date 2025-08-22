package knight.compiler.ast.program;

import java.util.List;

import knight.compiler.ast.AST;
import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTInterface extends AST
{
	private ASTIdentifier identifier;
	private ASTList<ASTFunction> functions;
	private ASTList<ASTIdentifier> extendedInterfaces;

	public ASTInterface(Token token, ASTIdentifier identifier, List<ASTFunction> functions,
			List<ASTIdentifier> extendedInterfaces)
	{
		super(token);
		this.identifier = identifier;
		this.functions = new ASTList<>(functions);
		this.extendedInterfaces = new ASTList<>(extendedInterfaces);
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

	public ASTFunction getFunction(int index)
	{
		return functions.getAt(index);
	}

	public int getFunctionCount()
	{
		return functions.getSize();
	}

	public List<ASTIdentifier> getExtendedInterfaces()
	{
		return extendedInterfaces.getList();
	}

	public ASTIdentifier getExtendedInterface(int index)
	{
		return extendedInterfaces.getAt(index);
	}

	public int getExtendedInterfaceCount()
	{
		return extendedInterfaces.getSize();
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}
