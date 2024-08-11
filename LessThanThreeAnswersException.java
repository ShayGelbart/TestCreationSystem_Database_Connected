package testing;

import java.io.Serial;

public class LessThanThreeAnswersException extends Exception{

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	public LessThanThreeAnswersException(String msg) {
		super(msg);
	}
	
	public LessThanThreeAnswersException() {
		super("You tried choosing an American question with three or less answers,you cannot");
	}
}
