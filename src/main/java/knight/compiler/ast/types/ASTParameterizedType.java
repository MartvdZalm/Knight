package knight.compiler.ast.types;

import knight.compiler.ast.utils.ASTList;
import knight.compiler.ast.ASTVisitor;
import knight.compiler.lexer.Token;

import java.util.List;

public class ASTParameterizedType extends ASTType
{
	private ASTIdentifierType baseType;
	private ASTList<ASTType> templateArguments;

	public ASTParameterizedType(Token token, ASTIdentifierType baseType, List<ASTType> templateArguments)
	{
		super(token);
		this.baseType = baseType;
		this.templateArguments = new ASTList<>(templateArguments);
	}

	public ASTIdentifierType getBaseType()
	{
		return baseType;
	}

	public void setBaseType(ASTIdentifierType baseType)
	{
		this.baseType = baseType;
	}

	public ASTList<ASTType> getTemplateArguments()
	{
		return templateArguments;
	}

	public void setTemplateArguments(ASTList<ASTType> templateArguments)
	{
		this.templateArguments = templateArguments;
	}

	@Override
	public <R> R accept(ASTVisitor<R> v)
	{
		return v.visit(this);
	}

	@Override
	public String toString()
	{
		return baseType + "<" + templateArguments.join(", ") + ">";
	}
}
