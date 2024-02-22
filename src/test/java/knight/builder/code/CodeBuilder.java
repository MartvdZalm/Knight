package knight.builder.code;

import knight.builder.Builder;
import com.github.javafaker.Faker;

public class CodeBuilder extends Builder
{
	protected Faker faker;

	protected void initialize()
	{
		this.faker = new Faker();
	}

    public Boolean empty(String str)
    {
    	if (str == null || str.isEmpty()) {
    		return true;
		} else {
			return false;
		}
    }
}