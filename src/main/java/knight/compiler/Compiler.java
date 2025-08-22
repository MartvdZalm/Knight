package knight.compiler;

import knight.compiler.ast.AST;
import knight.compiler.ast.program.ASTProgram;
import knight.compiler.ast.ASTSourceFileSetter;
import knight.compiler.lexer.Lexer;
import knight.compiler.parser.Parser;
import knight.compiler.semantics.BuildSymbolTree;
import knight.compiler.semantics.NameAnalyser;
import knight.compiler.semantics.TypeAnalyser;
import knight.compiler.semantics.model.SymbolProgram;
import knight.compiler.library.LibraryManager;

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

				if (tree instanceof ASTProgram) {
					ASTProgram astProgram = (ASTProgram) tree;
					// Set the source file for the entire AST tree
					setSourceFileRecursively(astProgram, file.getCanonicalPath());
					astPrograms.add(astProgram);
				}

				reader.close();
			} catch (Exception e) {
				System.err.println("Error parsing file: " + file.getPath() + " - " + e.getMessage());
				e.printStackTrace();
			}
		}

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

		// First, load all libraries into the symbol program
		// LibraryManager.loadAllLibraries(symbolProgram);

		BuildSymbolTree buildSymbolTree = new BuildSymbolTree(symbolProgram);
		for (ASTProgram astProgram : astPrograms) {
			buildSymbolTree.visit(astProgram);
		}

		return symbolProgram;
	}

	public void semantics(List<ASTProgram> astPrograms, SymbolProgram symbolProgram)
	{
		// Run semantics analysis on all programs with the unified symbol program
		for (ASTProgram astProgram : astPrograms) {
			NameAnalyser nameAnalyser = new NameAnalyser(symbolProgram);
			nameAnalyser.visit(astProgram);

			TypeAnalyser typeAnalyser = new TypeAnalyser(symbolProgram);
			typeAnalyser.visit(astProgram);
		}
	}
}
