package testing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ManualExam implements Examable {

    public static boolean createExam(String subjectName, int numOfQuestions, Connection connection, Scanner sc) {
        try {
            int qIndex, testId = Test.insertToTable(connection, subjectName), qId;
            boolean isAmerican = false;
            if (testId == 0) {
                System.out.println("An error occurred, please try again");
                return false;
            }
            System.out.println(Pool.questionPoolToString(subjectName, connection));
            int numOfQuestionsInPool;
            while (numOfQuestions > 0) {
                do {
                    System.out.println("Enter the question's index which you want to add to the test");
                    qIndex = sc.nextInt();
                    numOfQuestionsInPool = Pool.getAmountOfQuestionsInSubjectPool(connection, subjectName);
                    if (numOfQuestionsInPool == -1)
                        System.out.println("An error occurred, please try again");
                } while ((qIndex <= 0 || qIndex > numOfQuestionsInPool));

                qId = Pool.getQuestionArrayAtIndex(qIndex, connection, subjectName);
                if (Pool.isQuestionType(connection, qId, "AmericanQuestion")) {
                    isAmerican = true;
                    if (AmericanQuestion.getAnswerCount(connection, qId) <= 3) {
                        throw new LessThanThreeAnswersException();
                    }
                } else
                    isAmerican = false;

                int resAddToTest = Test.addQuestionToTestArray(testId, qId, isAmerican, connection);
                if (resAddToTest == 1) {
                    System.out.println("Successfully added the question to the test");
                    numOfQuestions--;
                } else if (resAddToTest == 0)
                    System.out.println("Question is already in the test, try again with a different question");
                else {
                    System.out.println("An error occurred, please try again");
                    return false;
                }
            }

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm");
            String fileName = now.format(formatter) + "_Manual_" + subjectName;
            String fileNameExam = fileName + "_exam.txt";
            String fileNameSolution = fileName + "_solution.txt";

            File exam = new File(fileNameExam);
            exam.createNewFile();
            PrintWriter pw = new PrintWriter(exam);
            pw.print(Test.examToFile(connection, testId));
            pw.close();

            File solution = new File(fileNameSolution);
            solution.createNewFile();
            PrintWriter pwr = new PrintWriter(solution);
            pwr.print(Test.solutionQuestionsToString(connection, testId));
            pwr.close();

        } catch (IOException e) {
            System.out.println("An I/O error occurred: " + e.getMessage());
            return false;
        } catch (LessThanThreeAnswersException e) {
            System.out.println("A question has less than three answers: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            return false;
        }
        return true;
    }

}
