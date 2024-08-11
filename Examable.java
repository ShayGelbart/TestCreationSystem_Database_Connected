package testing;

import java.io.IOException;

public interface Examable {
	
	public boolean createExam(Actions a, int numOfQuestions) throws IOException, LessThanThreeAnswersException;
	
}
