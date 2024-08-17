package testing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class AutomaticExam implements Examable {

    public static boolean createExam(String subjectName, int numOfQuestions, Connection connection , Scanner sc) throws SQLException, IOException {
        int qRandIndex, answerCount = 0, aRandIndex, qArrayIndex = 1, testId, questionId, indexWhile = 4, reassignedCheck;
        boolean correctAnswerFlag = false, isAnswerTrue = false, isAmerican = false;
        testId = Test.insertToTable(connection, subjectName);
        if (testId == 0) // exception
            return false;


        while (numOfQuestions > 0) { // getting as much questions as input
            reassignedCheck = Pool.getAmountOfQuestionsInSubjectPool(connection, subjectName);
            if (reassignedCheck == -1)
                return false;

            qRandIndex = (int) (Math.random() * reassignedCheck) + 1;// random index for question
            questionId = Pool.getQuestionArrayAtIndex(qRandIndex, connection, subjectName);

            if (!Test.isQuestionInQuestionPool(testId, questionId, connection)) {
                if (Pool.isQuestionType(connection, questionId, "AmericanQuestion")) {
                    isAmerican = true;
                    try {
                        answerCount = AmericanQuestion.getAnswerCount(connection, questionId);
                        if (answerCount == -1) // exception
                            return false;
                        if (answerCount <= 3)
                            throw new LessThanThreeAnswersException();
                    } catch (LessThanThreeAnswersException e) {
                        System.out.println(e.getMessage());
                    }

                    int answerCountInPool = Pool.getAmountOfAnswersInSubjectPool(connection, subjectName);
                    if (answerCountInPool <= 0)
                        return false;

                    while (indexWhile > 0) { // getting answers
                        aRandIndex = (int) (Math.random() * answerCountInPool + 1);
                        String answerText = Pool.getAnswerTextArrayAtIndex(connection, subjectName, aRandIndex);
                        if (answerText == null)
                            return false;

                        if (AmericanQuestion.isAnswerTrue(connection, questionId, answerText)) {
                            if (!correctAnswerFlag) { // only one answer can be true
                                correctAnswerFlag = true;
                                isAnswerTrue = true;
                            }
                        } else
                            isAnswerTrue = false;

                        if (!Test.isAnswerTextInAmericanQuestion(testId, questionId, answerText, connection)) {
                            reassignedCheck = Test.addAnswerToQuestion(testId, answerText, questionId, isAnswerTrue, connection);
                            if (reassignedCheck == -1)
                                return false;
                            if (reassignedCheck == 1)
                                indexWhile--;
                        }
                    }
                } else { // open question
                    isAmerican = false;
                    String answerToOpenQuestion = OpenQuestion.getOpenQuestionSolution(questionId, connection);
                    reassignedCheck = Test.addAnswerToQuestion(testId, answerToOpenQuestion, questionId, isAnswerTrue, connection);
                    if (reassignedCheck == -1)
                        return false;
                }

                reassignedCheck = Test.addQuestionToTestArray(testId, questionId, isAmerican, connection);
                if (reassignedCheck == -1)
                    return false;
                if (reassignedCheck == 1)
                    numOfQuestions--;
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
