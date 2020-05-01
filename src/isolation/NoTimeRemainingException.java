package isolation;

public class NoTimeRemainingException extends Exception
{
	public NoTimeRemainingException(String errorMessage)
	{
		super(errorMessage);
	}
}

