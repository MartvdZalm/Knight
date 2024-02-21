package knight.builder.code;

import knight.builder.Builder;

public class CodeBuilder extends Builder
{
    public Boolean empty(String str)
    {
    	if (str == null || str.isEmpty()) {
    		return true;
		} else {
			return false;
		}
    }
}