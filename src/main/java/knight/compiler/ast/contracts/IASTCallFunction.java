package knight.compiler.ast.contracts;

import java.util.List;
import knight.compiler.ast.expressions.ASTExpression;
import knight.compiler.ast.expressions.ASTIdentifierExpr;
import knight.compiler.ast.types.ASTType;

public interface IASTCallFunction extends HasToken
{
	public ASTIdentifierExpr getInstance();

	public void setInstance(ASTIdentifierExpr instance);

	public ASTIdentifierExpr getFunctionName();

	public void setFunctionName(ASTIdentifierExpr functionName);

	public void setArguments(List<ASTExpression> arguments);

	public List<ASTExpression> getArguments();

	public int getArgumentCount();

	public ASTExpression getArgument(int index);
}
