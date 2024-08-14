package testing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AutomaticExam implements Examable {

	@Override
	public boolean createExam(String subjectName, int numOfQuestions) throws IOException {
		int qRandIndex, correctAnswerCounter = 0, answerCount, aRandIndex, qArrayIndex = 1;
		//Actions b = new Actions(a);
		//Actions c = new Actions(a.getSubName());
		Test t = new Test(new ArrayList<Question>() , c, c.getSubName()); // empty test
		if(a.countAmericanQuestionsWithMoreThanFourAnswers() < numOfQuestions)
			return false;
		while(t.getTestQuestions().size() < numOfQuestions) { // getting as much questions as input
			qRandIndex = (int) (Math.random() * b.getQuestionArray().size()) + 1;// random index for question
			if (a.getQuestionArrayAtIndex(qRandIndex) instanceof AmericanQuestion) {
				AmericanQuestion q = new AmericanQuestion((AmericanQuestion) b.getQuestionArrayAtIndex(qRandIndex));				
				AmericanQuestion qToTest = new AmericanQuestion(q.getQuestionText(), q.getDiff());
				answerCount = q.getAnswerCount();
				if (answerCount >= 4 && q.getNumOfInorrectAnswers() >= 3) {
					correctAnswerCounter = 0;
					while(qToTest.getAnswerCount() < 4) { // getting answers
						answerCount = q.getAnswerCount();
						aRandIndex = (int) (Math.random() * answerCount + 1);
						if (q.getAnswerByIndex(aRandIndex).getTrueness()) {
							if (correctAnswerCounter == 0) {
								correctAnswerCounter++;
								qToTest.addAnswerToQuestion(q.getAnswerByIndex(aRandIndex).getAnswer(), true);
							} 
						} else
							qToTest.addAnswerToQuestion(q.getAnswerByIndex(aRandIndex).getAnswer(), false);
						q.deleteAnswerFromQuestion(aRandIndex);
					}
					c.addQuestionToPool(qToTest);
					b.deleteQuestionFromArray(qRandIndex);
					t.addQuestionToTestArray(qArrayIndex);
					qArrayIndex++;
				}
			}
		}
		// writing to files
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm");
		String fileName = now.format(formatter) + "_Auto_" + t.getSubName();
		String fileNameExam = fileName + "_exam.txt";
		String fileNameSolution = fileName + "_solution.txt";

		File exam = new File(fileNameExam);
		exam.createNewFile();
		PrintWriter pw = new PrintWriter(exam);
		pw.print(t.AutoFileAddedAnswersToString());
		pw.close();
	
		//t.creatingSolutionQuestionsArray();
		File solution = new File(fileNameSolution);
		solution.createNewFile();
		PrintWriter pwr = new PrintWriter(solution);
		pwr.print(t.solutionQuestionsToString());
		pwr.close();
		return true;
	}
}
