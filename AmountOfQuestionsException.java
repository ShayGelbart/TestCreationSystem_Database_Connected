package testing;

import java.io.Serial;

public class AmountOfQuestionsException extends Exception{

	
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	public AmountOfQuestionsException(String msg) {
		super(msg);
	}
	
	public AmountOfQuestionsException() {
		super("You tried making a test with more than 10 questions,you cannot");
	}
}
