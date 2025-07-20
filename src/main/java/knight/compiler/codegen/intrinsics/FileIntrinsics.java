package knight.compiler.codegen.intrinsics;

import java.util.List;

public class FileIntrinsics
{
	public static class FileHandler implements IntrinsicHandler
	{
		@Override
		public String generate()
		{
			StringBuilder cpp = new StringBuilder();
			return cpp.toString();
		}
	}
}
