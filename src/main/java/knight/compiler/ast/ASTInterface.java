package knight.compiler.ast;

import java.util.List;

import knight.compiler.lexer.Token;

public class ASTInterface extends AST
{
	private ASTIdentifier interfaceName;
	private ASTList<ASTFunction> methodSignatures;
	private ASTList<ASTIdentifier> extendsInterfaces;

	public ASTInterface(Token token, ASTIdentifier interfaceName, List<ASTFunction> methodSignatures,
			List<ASTIdentifier> extendsInterfaces)
	{
		super(token);
		this.interfaceName = interfaceName;
		this.methodSignatures = new ASTList<>(methodSignatures);
		this.extendsInterfaces = new ASTList<>(extendsInterfaces);
	}

	public ASTIdentifier getName()
	{
		return interfaceName;
	}

	public List<ASTFunction> getMethodSignatures()
	{
		return methodSignatures.getList();
	}

	public List<ASTIdentifier> getExtendedInterfaces()
	{
		return extendsInterfaces.getList();
	}

	@Override
	public <R> R accept(ASTVisitor<R> visitor)
	{
		return visitor.visit(this);
	}
}