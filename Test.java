package testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Test {
    private ArrayList<Question> testQuestions;
    private ArrayList<Question> solutionQuestions;
    private Pool a;
    private String subName;


    public Test(String subName) {
        this.testQuestions = new ArrayList<>();
        //this.a = a;
        this.subName = subName;
    }

    public static int insertToTable(Connection connection, String subjectName) {
        PreparedStatement pst;
        try {
            pst = connection.prepareStatement("INSERT INTO Test (subjectName) VALUES (?) RETURNING testId");
            pst.setString(1, subjectName);
            ResultSet rs = pst.executeQuery();
            int res = 0;
            if (rs.next()) {
                res = rs.getInt(1);
            }
            pst.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getAmountofQuestionsInTest(Connection connection, int testId) {
        try (PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM TestQuestions WHERE testId LIKE ?")) {
            pst.setInt(1, testId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            rs.close();
            pst.close();
            return rs.getInt(1);
        } catch (SQLException e) {
            return -1;
        }
    }

    public void setA(Pool a) {
        this.a = a;
    }

    public void setTestQuestions(ArrayList<Question> testQuestions) {
        this.testQuestions = testQuestions;
    }

    public void setSolutionQuestions(ArrayList<Question> solutionQuestions) {
        this.solutionQuestions = solutionQuestions;
    }

    public Pool getA() {
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
    public static int addQuestionToTestArray(int testId, int qId, boolean isAmerican, Connection connection) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("INSERT INTO TestQuestions VALUES (?, ?, ?)");
            pst.setInt(1, testId);
            pst.setInt(2, qId);
            pst.setBoolean(3, isAmerican);
            int res = pst.executeUpdate();
            pst.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int addAnswerToQuestion(String answerText, int qId, boolean trueness, Connection connection) {
        String sql = "INSERT INTO TestQuestionAnswer (questionId, answerText, trueness) VALUES (?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, qId);
            pst.setString(2, answerText);
            pst.setBoolean(3, trueness);

            int result = pst.executeUpdate();
            pst.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isAmericanQuestion(int testId, int questionId, Connection connection) {
        String sql = "SELECT isAmerican FROM TestQuestions WHERE testId = ? AND questionId = ?";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, testId);
            pst.setInt(2, questionId);

            ResultSet rs = pst.executeQuery();

            if (rs.next())
                return rs.getBoolean("isAmerican");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
    public static String creatingSolutionQuestionsArray(Connection connection, int questionId) throws SQLException {
        String str = "";
        int trueAnswerCounter = 0;
        String tempAnswerText = null;
        String zeroCorrect = "Not a single answer is correct";
        String moreThanOne = "More than one answer is correct";

        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Query to get all answers for the specific American question from QuestionAnswer and Answer tables
            pst = connection.prepareStatement(
                    "SELECT A.trueness, A.answerText " +
                            "FROM QuestionAnswer QA " +
                            "JOIN Answer A ON QA.answerText = A.answerText " +
                            "WHERE QA.questionId = ?"
            );
            pst.setInt(1, questionId);
            rs = pst.executeQuery();

            // Count how many correct answers there are
            while (rs.next()) {
                boolean isTrue = rs.getBoolean("trueness"); // Get the truth value of the answer
                if (isTrue) {
                    trueAnswerCounter++;
                    tempAnswerText = rs.getString("answerText");  // Store the answer text of the correct answer
                }
            }

            // Determine the result based on the number of correct answers
            if (trueAnswerCounter == 0) {
                str = zeroCorrect;
            } else if (trueAnswerCounter == 1) {
                str = tempAnswerText;  // Use the correct answer text if there's only one
            } else if (trueAnswerCounter > 1) {
                str = moreThanOne;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }

        return str + "\n";
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
    public static String solutionQuestionsToString(Connection connection, int testId) throws SQLException {
        int i = 0, questionId;
        //setSolutionQuestions(testQuestions);
        String str = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT * FROM TestQuestions t JOIN Question q ON t.questionId = q.questionId WHERE testId = ?");
            pst.setInt(1, testId);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        while (rs.next()) {
            questionId = rs.getInt("questionId");
            str = (i + 1) + ")";
            if (isAmericanQuestion(testId, questionId, connection)) {
                str += rs.getString("questionText") + "\n" + creatingSolutionQuestionsArray(connection, questionId);
            } else {
                str += OpenQuestion.getOpenQuestionTextAndDiff(questionId, connection) + OpenQuestion.getOpenQuestionSolution(questionId, connection);
            }
        }
        rs.close();
        if (pst != null) pst.close();
        return str;
    }

    // print the "more than one correct answer" and the "no correct answers" along
    // with the test questions and their answers
    public static String manualFileAddedAnswersToString(Connection connection, int testId) throws SQLException { // test to file
        int i = 0;
        String str = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT * FROM TestQuestions t JOIN Question q ON t.questionId = q.questionId WHERE testId = ?");
            pst.setInt(1, testId);
            rs = pst.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        int questionId;
        while (rs.next()) {
            questionId = rs.getInt("questionId");
            str = rs.getString("difficulty") + ": " + rs.getString("questionText") + "\n";
            if (isAmericanQuestion(testId, questionId, connection)) {
                str = "(American question), " + str + AmericanQuestion.getAmericanQuestionAnswers(connection, questionId);
                str += "Answer-Not a single answer is correct\n";
                str += "Answer-More than one answer is correct\n\n";
            } else // open question
                str = "(Open question), " + str;
            str = (i + 1) + ")" + str;
            i++;
        }
        rs.close();
        pst.close();
        return str;
    }

    public static String AutoFileAddedAnswersToString(Connection connection, int testId) throws SQLException { // test to file
        int i = 0;
        String str = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT * FROM TestQuestions WHERE testId = ?");
            pst.setInt(1, testId);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int questionId;
        while (rs.next()) {
            questionId = rs.getInt("questionId");
            str = rs.getString("difficulty") + ": " + rs.getString("answerText") + "\n";
            if (isAmericanQuestion(testId, questionId, connection)) {
                str = "(American question), " + str + AmericanQuestion.getAmericanQuestionAnswers(connection, questionId);
                str += "Answer-Not a single answer is correct\n";
                str += "Answer-More than one answer is correct\n\n";
            } else // open question
                str = "(Open question), " + str;
            str = (i + 1) + ")" + str;
            i++;
        }
        rs.close();
        pst.close();
        return str;
    }


}
