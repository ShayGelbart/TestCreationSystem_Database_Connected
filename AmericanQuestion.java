package testing;

import java.io.Serial;
import java.util.ArrayList;

public class AmericanQuestion extends Question {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private ArrayList<Answer> answersForQuestions = new ArrayList<>();

	public AmericanQuestion(String questionText , Difficulty diff) {
		super(questionText);
		this.diff = diff;
	}
	
	public AmericanQuestion(AmericanQuestion other) {
	    super(other.questionText); 
	    this.diff = other.diff; 

	    // Create a new ArrayList for answersForQuestions and copy the contents
	    this.answersForQuestions = new ArrayList<>();
	    for (Answer answer : other.answersForQuestions) {
	        this.answersForQuestions.add(new Answer(answer));
	    }
	}

	private void setAnswerTextArray(ArrayList<Answer> answersForQuestions) {
		this.answersForQuestions = answersForQuestions;
	}

    @Override
	public int getAnswerCount() {
		return answersForQuestions.size();
	}
	
	public Answer getAnswerByIndex(int index) {
		return answersForQuestions.get(index - 1);
	}

	public ArrayList<Answer> getAnswersForQuestions() {
		return answersForQuestions;
	}
	
	public int getNumOfCorrectAnswers() {
		int counter=0;
        for (Answer answersForQuestion : answersForQuestions)
            if (answersForQuestion.getTrueness())
                counter++;
		return counter;
	}
	
	public int getNumOfInorrectAnswers() {
		int counter=0;
        for (Answer answersForQuestion : answersForQuestions)
            if (!answersForQuestion.getTrueness())
                counter++;
		return counter;
	}
	
	// add answer to question
	@Override
	public boolean addAnswerToQuestion(AnswerText answerText, boolean trueness) {
		if (answersForQuestions.size() == 10)
			return false;
        for (Answer answersForQuestion : answersForQuestions)
            if (answersForQuestion.getAnswer().equals(answerText))
                return false;
		Answer addedAnswer = new Answer(answerText, trueness);
		answersForQuestions.add(addedAnswer);
		setAnswerTextArray(answersForQuestions);
		return true;
	}

	// delete answer to question via index
	public boolean deleteAnswerFromQuestion(int index) {
		if (index > answersForQuestions.size() || index <= 0) {
			System.out.println("Failed to delete answer");
			return false;
		}
		answersForQuestions.remove(index - 1);
		System.out.println("successfully deleted answer");
		return true;
	}

	// deletes all of a question's answers
	public void deleteAllAnswers() {
		answersForQuestions.clear();
	}
	
	@Override
	public String questionWithAnswersToString() {
		String str = diff.name() + ", American question, " + super.toString() + "\n";
        for (Answer answersForQuestion : this.answersForQuestions) {
            str += answersForQuestion.toString() + "\n";
        }
		return str;
	}
		
	public String toString() {
		int i = 0;
		StringBuilder str = new StringBuilder(super.toString() + "\n(American question) \n");
        for (Answer answersForQuestion : this.answersForQuestions) {
            str.append((i + 1)).append(")").append(answersForQuestion.toString()).append("\n");
            i++;
        }
		return str.toString();

	}

	protected String testToString() {
		String str ="(American question) \n" + super.testToString() + "\n";
        for (Answer answersForQuestion : answersForQuestions)
			str += answersForQuestion.getAnswer().toString() + "\n";

		return str;
	}
}
