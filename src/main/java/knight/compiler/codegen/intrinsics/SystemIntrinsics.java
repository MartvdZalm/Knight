package knight.compiler.codegen.intrinsics;

import java.util.List;

public class SystemIntrinsics
{
	public static class SystemHandler implements IntrinsicHandler
	{
		@Override
		public String generate()
		{
			StringBuilder cpp = new StringBuilder();

			return cpp.toString();
		}
	}
}