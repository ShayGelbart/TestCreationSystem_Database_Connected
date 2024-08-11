package testing;

public class OpenQuestion extends Question {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AnswerText schoolSolution;

	public OpenQuestion(String questionText , AnswerText schoolSolution , Difficulty diff) {
		super(questionText);
		this.diff = diff;
		this.schoolSolution = schoolSolution;
		
	}
	public void setSchoolSolution(AnswerText schoolSolution) {
		this.schoolSolution = schoolSolution;
	}
	public AnswerText getSchoolSolution() {
		return schoolSolution;
	}

    public String testToString() {
		return diff.name() + ", open question, " + super.toString();
	}
	
	public String toString() {
		return diff.name() + ", open question, " + super.toString() + "\nAnswer-" + schoolSolution + "\n"; 
	}
	
	// Overridden functions
	
	@Override
	public int getAnswerCount() {
		return 0;
	}

	@Override
	public boolean addAnswerToQuestion(AnswerText answerText, boolean answerIsTrue) {
		return false;
	}

	@Override
	public boolean deleteAnswerFromQuestion(int indexAnswer) {
		return false;
	}

	@Override
	public void deleteAllAnswers() {		
	}

	@Override
	public Answer getAnswerByIndex(int index) {
		return null;
	}

	
	@Override
	public String questionWithAnswersToString() {
		return super.testToString() + "\nAnswer-" + schoolSolution + "\n";
	}
}
