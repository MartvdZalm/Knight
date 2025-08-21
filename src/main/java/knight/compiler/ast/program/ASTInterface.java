package knight.compiler.ast.program;

import java.util.List;

import knight.compiler.ast.AST;
import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

public class ASTInterface extends AST
{
	private ASTIdentifier interfaceName;
	private ASTList<ASTFunction> functionSignatures;
	private ASTList<ASTIdentifier> extendsInterfaces;

	public ASTInterface(Token token, ASTIdentifier interfaceName, List<ASTFunction> functionSignatures,
			List<ASTIdentifier> extendsInterfaces)
	{
		super(token);
		this.interfaceName = interfaceName;
		this.functionSignatures = new ASTList<>(functionSignatures);
		this.extendsInterfaces = new ASTList<>(extendsInterfaces);
	}

	public ASTIdentifier getName()
	{
		return interfaceName;
	}

	public List<ASTFunction> getFunctionSignatures()
	{
		return functionSignatures.getList();
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