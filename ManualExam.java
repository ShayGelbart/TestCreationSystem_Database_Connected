package testing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class ManualExam implements Examable {
    static Scanner sc = new Scanner(System.in);

    @Override
    public boolean createExam(Actions a, int numOfQuestions) {
        try {
            int qIndex;
            Actions b = new Actions(a);
            Test t = new Test(new ArrayList<Question>(), b, b.getSubName());
            System.out.println(b.toString());
            while (t.getTestQuestions().size() < numOfQuestions) {
                do {
                    System.out.println("Enter the question's index which you want to add to the test");
                    qIndex = sc.nextInt();
                } while (qIndex <= 0 || qIndex > b.getQuestionArray().size());

                if (b.getQuestionArrayAtIndex(qIndex) instanceof AmericanQuestion) {
                    if (b.getQuestionArrayAtIndex(qIndex).getAnswerCount() <= 3)
                        throw new LessThanThreeAnswersException();
                }

                if (t.addQuestionToTestArray(qIndex))
                    System.out.println("Successfully added the question to the test");
                else
                    System.out.println("Failed to add the question to the test, try again with a different question");
            }

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm");
            String fileName = now.format(formatter) + "_Manual_" + t.getSubName();
            String fileNameExam = fileName + "_exam.txt";
            String fileNameSolution = fileName + "_solution.txt";

            File exam = new File(fileNameExam);
            exam.createNewFile();
            PrintWriter pw = new PrintWriter(exam);
            pw.print(t.manualFileAddedAnswersToString());
            pw.close();

            File solution = new File(fileNameSolution);
            solution.createNewFile();
            PrintWriter pwr = new PrintWriter(solution);
            pwr.print(t.solutionQuestionsToString());
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
