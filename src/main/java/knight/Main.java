package knight;

import knight.playground.CompileResult;
import knight.playground.CompilerFacade;
import knight.utils.FileHelper;

public class Main
{
	public static void main(String[] args)
	{
		if (args.length < 1) {
			System.err.println("Usage: java Main <filename>.knight");
			System.exit(1);
		}

		Main main = new Main();
		main.compile(args);
	}

	public void compile(String[] args)
	{
		String filename = args[0];

		if (!FileHelper.isFileValid(filename)) {
			return;
		}

		System.out.println("Compiling: " + filename);

		CompileResult result = CompilerFacade.compileFromPath(filename);

		if (!result.isSuccess()) {
			System.err.println("\nCompilation failed with errors:");
			for (String error : result.getErrors()) {
				System.err.println(error);
			}
			if (!result.getWarnings().isEmpty()) {
				System.out.println("\nWarnings:");
				for (String warning : result.getWarnings()) {
					System.out.println(warning);
				}
			}
			System.exit(1);
		}

		if (!result.getWarnings().isEmpty()) {
			System.out.println("\nWarnings:");
			for (String warning : result.getWarnings()) {
				System.out.println(warning);
			}
		}

		String path = FileHelper.getFileDirPath(filename);
		FileHelper.write(result.getGeneratedCpp(), path, filename);

		System.out.println("Code generation completed successfully");
		System.out.println("Output written to: " + path + FileHelper.removeFileExtension(filename) + ".cpp");
	}
}
