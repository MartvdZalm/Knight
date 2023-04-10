package src;
import java.io.File;

import src.ast.Program;
import src.ast.Tree;
import src.parser.Parser;
import src.semantics.NameError;
import src.semantics.SemanticErrors;
import src.symbol.SymbolTable;
import src.visitor.BuildSymbolTableVisitor;
import src.visitor.CodeGenerator;
import src.visitor.NameAnalyserTreeVisitor;
import src.visitor.TypeAnalyser;

public class Main
{
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java Main <filename>.knight");
			System.exit(1);
		}
	
		Main main = new Main();
		main.codeGen(args[0]);
	}

	public void codeGen(String str)
	{
		try {
			if (!isFileValid(str)) {
				return;
			}

			Parser p = new Parser(str);
			Tree tree = p.parse();

			if (tree != null) {
				BuildSymbolTableVisitor bstv = new BuildSymbolTableVisitor();
				bstv.visit((Program) tree);

				SymbolTable st = bstv.getSymTab();
				
				NameAnalyserTreeVisitor natv = new NameAnalyserTreeVisitor(st);
				natv.visit((Program) tree);

				TypeAnalyser ta = new TypeAnalyser(st);
				ta.visit((Program) tree);

				if (SemanticErrors.errorList.size() == 0) {
					String path = getFileDirPath(str);
					CodeGenerator cg = new CodeGenerator(path);
					cg.visit((Program) tree);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (SemanticErrors.errorList.size() != 0) {
				SemanticErrors.sort();
				for (NameError e : SemanticErrors.errorList) {
					System.err.println(e);
				}
			}
		}
	}

    private boolean isFileValid(String filename)
	{
		File f = new File(filename);

		if (!f.exists()) {
			System.err.println(filename + ": No such file!");
			return false;
		}

		String fileExtension = getFileExtension(f);
		if (!"knight".equals(fileExtension)) {
			System.err.println(filename + ": Invalid file extension!");
			return false;
		}

		return true;
	}

    private String getFileExtension(File file)
	{
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	private String getFileDirPath(String filename)
	{
		try {
			File f = new File(filename);
			String path = f.getParent();
			if (path == null) {
				return "";
			}
			return path + File.separator;
		} catch (Exception e) {
			return "";
		}
	}
}