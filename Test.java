package testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test {
    //inserts test into database
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
    //returns amount of questions in the test
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
    // checks if answer of american question is in the test questions table
    public static boolean isAnswerTextInAmericanQuestion(int testId, int questionId, String answerText, Connection connection) {
        try (PreparedStatement pst = connection.prepareStatement("SELECT 1 FROM TestQuestionAnswer qa " +
                "WHERE qa.testId = ? AND qa.questionId = ? AND qa.answerText = ?")) {
            pst.setInt(1, testId);
            pst.setInt(2, questionId);
            pst.setString(3, answerText);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next(); // Returns true if there is at least one result
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }
    //checks if question is in the question pool of the test
    public static boolean isQuestionInQuestionPool(int testId, int questionId, Connection connection) {
        try (PreparedStatement pst = connection.prepareStatement("SELECT 1 FROM TestQuestions " +
                "WHERE testId = ? AND questionId = ?")) {
            pst.setInt(1, testId);
            pst.setInt(2, questionId);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next(); // Returns true if there is at least one result
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
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
    //adds answer to the question in the test-question-answer table
    public static int addAnswerToQuestion(int testId, String answerText, int qId, boolean trueness, Connection connection) {
        String sql = "INSERT INTO TestQuestionAnswer (testId, questionId, answerText, trueness) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, testId);
            pst.setInt(2, qId);
            pst.setString(3, answerText);
            pst.setBoolean(4, trueness);

            int result = pst.executeUpdate();
            pst.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    //checks if the question is american in the test
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
                    "SELECT trueness, answerText " +
                            "FROM QuestionAnswer WHERE questionId = ?"
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


    // print only the question with their correct answer according to how many correct
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
            str += (i + 1) + ")";
            if (isAmericanQuestion(testId, questionId, connection)) {
                str += rs.getString("questionText") + "\n" + creatingSolutionQuestionsArray(connection, questionId);
            } else {
                str += OpenQuestion.getOpenQuestionTextAndDiff(questionId, connection) + "Solution: " + OpenQuestion.getOpenQuestionSolution(questionId, connection) + "\n";
            }
            str += "\n";
            i++;
        }
        rs.close();
        if (pst != null) pst.close();
        return str;
    }
    //returns string for test to print in file
    public static String examToFile(Connection connection, int testId) throws SQLException {
        StringBuilder sb = new StringBuilder(); // Use StringBuilder for efficient string concatenation
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            pst = connection.prepareStatement(
                    "SELECT q.questionId, q.questionText, q.difficulty " +
                            "FROM TestQuestions t " +
                            "JOIN Question q ON t.questionId = q.questionId " +
                            "WHERE t.testId = ?"
            );
            pst.setInt(1, testId);
            rs = pst.executeQuery();

            int i = 1; // Start numbering from 1
            while (rs.next()) {
                int questionId = rs.getInt("questionId");
                sb.append(i).append(")");
                String difficulty = rs.getString("difficulty");
                String questionText = rs.getString("questionText");

                sb.append(difficulty).append(": ").append(questionText).append("\n");

                if (isAmericanQuestion(testId, questionId, connection)) {
                    sb.append("(American question), ");
                    sb.append(AmericanQuestion.getAmericanQuestionAnswers(connection, questionId));
                    sb.append("Answer-Not a single answer is correct\n");
                    sb.append("Answer-More than one answer is correct\n\n");
                } else {
                    sb.append("(Open question)\n");
                }
                sb.append("\n");
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider rethrowing or handling the exception as needed
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }

        return sb.toString(); // Return the built string
    }
//    //returns string for print automatic exam
//    public static String autoExamToFile(Connection connection, int testId) throws SQLException {
//        StringBuilder sb = new StringBuilder(); // Use StringBuilder for efficient string concatenation
//        PreparedStatement pst = null;
//        ResultSet rs = null;
//
//        try {
//            pst = connection.prepareStatement(
//                    "SELECT q.questionId, q.questionText, q.difficulty " +
//                            "FROM TestQuestions t " +
//                            "JOIN Question q ON t.questionId = q.questionId " +
//                            "WHERE t.testId = ?"
//            );
//            pst.setInt(1, testId);
//            rs = pst.executeQuery();
//
//            int i = 1;
//            while (rs.next()) {
//                int questionId = rs.getInt("questionId");
//                sb.append(i).append(")");
//                String difficulty = rs.getString("difficulty");
//                String questionText = rs.getString("questionText");
//
//                sb.append(difficulty).append(": ").append(questionText).append("\n");
//
//                if (isAmericanQuestion(testId, questionId, connection)) {
//                    sb.append("(American question)\n");
//                    sb.append(AmericanQuestion.getAmericanQuestionAnswers(connection, questionId));
//                    sb.append("Answer-Not a single answer is correct\n");
//                    sb.append("Answer-More than one answer is correct\n\n");
//                } else {
//                    sb.append("(Open question)\n");
//                }
//                sb.append("\n");
//                i++;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Consider rethrowing or handling the exception as needed
//        } finally {
//            if (rs != null) rs.close();
//            if (pst != null) pst.close();
//        }
//
//        return sb.toString(); // Return the built string
//    }
    //returns al the info from the test
    public static String getTestTable(Connection connection) {
        StringBuilder result = new StringBuilder("Test Table:\n");
        String query = "SELECT * FROM Test";
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int testId = rs.getInt("testId");
                String subjectName = rs.getString("subjectName");
                result.append("TestID: ").append(testId).append(", SubjectName: ").append(subjectName).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
