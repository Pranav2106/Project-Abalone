package exceptions;

public class ExitProgramException extends Exception{
	
	private static final long serialVersionUID = 4487322440921926620L;

	public ExitProgramException(String msg) {
		super(msg);
	}
}