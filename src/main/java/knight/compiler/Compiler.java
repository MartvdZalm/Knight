package knight.compiler;

import knight.compiler.ast.AST;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.lexer.Lexer;
import knight.compiler.parser.Parser;
import knight.compiler.semantics.BuildSymbolTree;
import knight.compiler.semantics.NameAnalyser;
import knight.compiler.semantics.TypeAnalyser;
import knight.compiler.semantics.model.SymbolProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Compiler
{
	public List<ASTProgram> parseFiles(List<File> sourceFiles)
	{
		List<ASTProgram> astPrograms = new ArrayList<>();

		for (File file : sourceFiles) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				Lexer lexer = new Lexer(reader);
				Parser parser = new Parser(lexer);
				AST tree = parser.parse();
				astPrograms.add((ASTProgram) tree);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		return astPrograms;
	}

	public SymbolProgram buildSymbolProgram(List<ASTProgram> astPrograms)
	{
		BuildSymbolTree buildSymbolTree = new BuildSymbolTree();
		for (ASTProgram astProgram : astPrograms) {
			buildSymbolTree.visit(astProgram);
		}
		return buildSymbolTree.getSymbolProgram();
	}

	public void semantics(List<ASTProgram> astPrograms, SymbolProgram symbolProgram)
	{
		for (ASTProgram astProgram : astPrograms) {
			NameAnalyser nameAnalyser = new NameAnalyser(symbolProgram);
			nameAnalyser.visit(astProgram);

			TypeAnalyser typeAnalyser = new TypeAnalyser(symbolProgram);
			typeAnalyser.visit(astProgram);
		}
	}
}
