package testing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AutomaticExam implements Examable {

    @Override
    public boolean createExam(String subjectName, int numOfQuestions, Connection connection) throws SQLException, IOException {
        int qRandIndex, correctAnswerCounter = 0, answerCount, aRandIndex, qArrayIndex = 1, testId, questionId, indexWhile = 4;
        //Actions b = new Actions(a);
        //Actions c = new Actions(a.getSubName());
        //Test t = new Test(new ArrayList<Question>() , c, c.getSubName()); // empty test
        testId = Test.insertToTable(connection, subjectName);
        if (testId == 0) // exception
            return false;

        int reassignedCheck = Pool.countAmericanQuestionsWithMoreThanFourAnswers(connection, subjectName);
        if (reassignedCheck < numOfQuestions)
            return false;

        while (numOfQuestions > 0) { // getting as much questions as input
            reassignedCheck = Pool.getAmountOfQuestionsInSubjectPool(connection, subjectName);
            if (reassignedCheck == -1)
                return false;

            qRandIndex = (int) (Math.random() * reassignedCheck) + 1;// random index for question
            questionId = Pool.getQuestionArrayAtIndex(qRandIndex, connection, subjectName);
            if (Pool.isQuestionType(connection, questionId, "AmericanQuestion")) {
                //AmericanQuestion q = new AmericanQuestion((AmericanQuestion) b.getQuestionArrayAtIndex(qRandIndex));
                //AmericanQuestion qToTest = new AmericanQuestion(q.getQuestionText(), q.getDiff());

                try {
                    if (AmericanQuestion.getAnswerCount(connection, questionId) <= 3)
                        throw new LessThanThreeAnswersException();
                } catch (LessThanThreeAnswersException e) {
                    System.out.println(e.getMessage());
                }

                answerCount = AmericanQuestion.getAnswerCount(connection, questionId);
                if (answerCount == -1) // exception
                    return false;

                if (answerCount >= 4 && AmericanQuestion.getNumOfIncorrectAnswers(connection, questionId) >= 3) {
                    while (indexWhile > 0) { // getting answers
                        answerCount = AmericanQuestion.getAnswerCount(connection, questionId);
                        aRandIndex = (int) (Math.random() * answerCount + 1);
                        if (AmericanQuestion.isAnswerTrue(connection, questionId, AmericanQuestion.getAnswerByIndex(connection, questionId, aRandIndex))) {
                            if (correctAnswerCounter == 0) {
                                correctAnswerCounter++;
                                reassignedCheck = AmericanQuestion.addAnswerToQuestion(AmericanQuestion.getAnswerByIndex(connection, questionId, aRandIndex), questionId, true, connection);
                                if (reassignedCheck == -1)
                                    return false;
                                if (reassignedCheck == 1)
                                    indexWhile--;
                            }
                        } else
                            AmericanQuestion.addAnswerToQuestion(AmericanQuestion.getAnswerByIndex(connection, questionId, aRandIndex), questionId, false, connection);
                        //q.deleteAnswerFromQuestion(aRandIndex);
                    }
                    //c.addQuestionToPool(qToTest);
                    //b.deleteQuestionFromArray(qRandIndex);
                    reassignedCheck = Test.addQuestionToTestArray(testId, questionId, connection);
                    if (reassignedCheck == -1)
                        return false;
                    if (reassignedCheck == 1)
                        numOfQuestions--;
                    //qArrayIndex++;
                }
            }
        }
        // writing to files
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm");
        String fileName = now.format(formatter) + "_Auto_" + subjectName;
        String fileNameExam = fileName + "_exam.txt";
        String fileNameSolution = fileName + "_solution.txt";

        File exam = new File(fileNameExam);
        exam.createNewFile();
        PrintWriter pw = new PrintWriter(exam);
        pw.print(Test.AutoFileAddedAnswersToString(connection, testId));
        pw.close();

        //t.creatingSolutionQuestionsArray();
        File solution = new File(fileNameSolution);
        solution.createNewFile();
        PrintWriter pwr = new PrintWriter(solution);
        pwr.print(Test.solutionQuestionsToString(connection, testId));
        pwr.close();
        return true;
    }
}
