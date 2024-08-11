package testing;

import java.util.ArrayList;

public class Test {
	private ArrayList<Question> testQuestions;
	private ArrayList<Question> solutionQuestions;
	private Actions a;
	private String subName;
	
	
	public Test(ArrayList<Question> testQuestions , Actions a , String subName) {
		this.testQuestions = testQuestions;
		this.a = a;
		this.subName = subName;
	}

	public void setA(Actions a) {
		this.a = a;
	}

	public void setTestQuestions(ArrayList<Question> testQuestions) {
		this.testQuestions = testQuestions;
	}

	public void setSolutionQuestions(ArrayList<Question> solutionQuestions) {
		this.solutionQuestions = solutionQuestions;
	}

	public Actions getA() {
		return a;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public ArrayList<Question> getTestQuestions() {
		return testQuestions;
	}

	public ArrayList<Question> getSolutionQuestions() {
		return solutionQuestions;
	}

	
	// add question to the test
	public boolean addQuestionToTestArray(int index) {
		for (int i = 0; i < testQuestions.size(); i++)
			if (a.getQuestionArrayAtIndex(index).getQuestionText().equals(testQuestions.get(i).getQuestionText()))
				return false;
		testQuestions.add(a.getQuestionArrayAtIndex(index));
		return true;
	}

	// delete question from the test
	public boolean deleteQuestionFromTestArray(int index) {
		if (index > testQuestions.size() || index <= 0) {
			return false;
		} else
			testQuestions.remove(index - 1);
		return true;
	}

	// creating the array which is used to print the solution file
	public String creatingSolutionQuestionsArray(int i) {
		setSolutionQuestions(testQuestions);
		String str = "";
		int trueAnswerCounter, temp = 0;
		String zeroCorrect = "Not a single answer is correct";
		String moreThanOne = "More than one answer is correct";
			trueAnswerCounter = 0;
			if (solutionQuestions.get(i) instanceof AmericanQuestion)
				for (int j = 1; j <= solutionQuestions.get(i).getAnswerCount(); j++)
					if (solutionQuestions.get(i).getAnswerByIndex(j).getTrueness()) {
						trueAnswerCounter++;
						temp = j;
					}
			if (trueAnswerCounter == 0)
				str = zeroCorrect;
			else if (trueAnswerCounter == 1)
				str = solutionQuestions.get(i).getAnswerByIndex(temp).getAnswer().getAnswerText();
			else if (trueAnswerCounter > 1)
				str = moreThanOne;
		return (str + "\n");	
	}

	// print only the question in the test without their answers
	public String testQuestionsToString() {
		int i = 0;
		String str = "Every question currently in the test:" + "\n";
        for (Question testQuestion : this.testQuestions) {
            str += (i + 1) + ")" + testQuestion.getQuestionText() + "\n";
            i++;
        }
		return str;
	}

	// print only the question with their fit answer according to how many correct
	// answers it has
	public String solutionQuestionsToString() {
		int i = 0;
		setSolutionQuestions(testQuestions);
		String str = "";
        for (Question q : this.solutionQuestions) {
            if (q instanceof AmericanQuestion)
                str += (i + 1) + ")" + q.getQuestionText() + "\n" + creatingSolutionQuestionsArray(i);
            else if(q instanceof OpenQuestion) // open question
                str += (i + 1) + ")" + q.questionWithAnswersToString();
            i++;
        }
		return str;
	}
	// print the "more than one correct answer" and the "no correct answers" along
	// with the test questions and their answers
	public String manualFileAddedAnswersToString() { // test to file
		int i = 0;
		String str = "";
        for (Question q : this.testQuestions) {
            if (q instanceof AmericanQuestion) {
                str += (i + 1) + ")" + q.testToString();
                str += "Answer-Not a single answer is correct" + "\n";
                str += "Answer-More than one answer is correct" + "\n" + "\n";
            } else // open question
                str += (i + 1) + ")" + q.testToString() + "\n";
            i++;
        }
		return str;
	}
	
	public String AutoFileAddedAnswersToString() { // test to file
		int i = 0;
		String str = "";

        for (Question q : this.testQuestions) {
            if (q instanceof AmericanQuestion) {
                str += (i + 1) + ")" + q.testToString();
                str += "Answer-Not a single answer is correct" + "\n";
            } else // open question
                str += (i + 1) + ")" + q.testToString();
            i++;
        }
		return str;
	}

	// print the test questions with their answers
	public String toString() {
		int i = 0;
		String str = "";

        for (Question q : this.solutionQuestions) {
            if (q instanceof AmericanQuestion) {
                str += (i + 1) + ")" + q.questionWithAnswersToString() + "\n";
            } else // open question
                str += (i + 1) + ")" + q.toString() + "\n";
            i++;
        }
		return str;
	}
}
