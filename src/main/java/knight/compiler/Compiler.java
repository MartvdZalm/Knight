package knight.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import knight.compiler.ast.AST;
import knight.compiler.ast.ASTSourceFileSetter;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.lexer.Lexer;
import knight.compiler.lexer.Symbol;
import knight.compiler.lexer.Token;
import knight.compiler.lexer.Tokens;
import knight.compiler.library.LibraryManager;
import knight.compiler.parser.Parser;
import knight.compiler.semantics.diagnostics.DiagnosticReporter;
import knight.compiler.semantics.BuildSymbolTree;
import knight.compiler.semantics.NameAnalyser;
import knight.compiler.semantics.TypeAnalyser;
import knight.compiler.semantics.model.SymbolProgram;

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

				if (tree instanceof ASTProgram) {
					ASTProgram astProgram = (ASTProgram) tree;
					setSourceFileRecursively(astProgram, file.getCanonicalPath());
					astPrograms.add(astProgram);
				}

				reader.close();
			} catch (Exception e) {
				System.err.println("Error parsing file: " + file.getPath() + " - " + e.getMessage());
				Token token = new Token(Symbol.symbol("EOF", Tokens.EOF), 1, 1);
				DiagnosticReporter.error(token, e.getMessage(), file.getPath());
			}
		}

		astPrograms.add(LibraryManager.loadStandardLibrary());

		return astPrograms;
	}

	private void setSourceFileRecursively(AST ast, String sourceFile)
	{
		if (ast == null)
			return;

		ASTSourceFileSetter setter = new ASTSourceFileSetter(sourceFile);
		setter.setSourceFileRecursively(ast);
	}

	public SymbolProgram buildSymbolProgram(List<ASTProgram> astPrograms)
	{
		SymbolProgram symbolProgram = new SymbolProgram();

		BuildSymbolTree buildSymbolTree = new BuildSymbolTree(symbolProgram);
		for (ASTProgram astProgram : astPrograms) {
			buildSymbolTree.visit(astProgram);
		}

		return symbolProgram;
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
