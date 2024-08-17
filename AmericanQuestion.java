package testing;

import java.io.Serial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AmericanQuestion {
    /**
     *
     */
    @Serial

    // returns number of answers in american question
    public static int getAnswerCount(Connection connection, int qId) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM QuestionAnswer WHERE questionId = ?")) {
            pst.setInt(1, qId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            int res = rs.getInt(1);
            rs.close();
            pst.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //returns text answer by index by row
    public static String getAnswerByIndex(Connection connection, int questionId, int index) throws SQLException {
        String answerText = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Prepare the SQL query to get the answer by index
            pst = connection.prepareStatement(
                    "SELECT answerText FROM QuestionAnswer WHERE questionId = ? LIMIT 1 OFFSET ?"
            );

            // Set the questionId and index (offset) parameters
            pst.setInt(1, questionId);
            pst.setInt(2, index - 1);  // Offset starts from 0, so subtract 1 from the index

            rs = pst.executeQuery();

            // Get the answer text if available
            if (rs.next()) {
                answerText = rs.getString("answerText");
            }

        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }

        return answerText;
    }

    //returns true if answer is true for the question
    public static boolean isAnswerTrue(Connection connection, int questionId, String answerText) throws SQLException {
        boolean isTrue = false;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Prepare the SQL query to check if the answer is true or false
            pst = connection.prepareStatement(
                    "SELECT trueness FROM QuestionAnswer WHERE questionId = ? AND answerText = ?"
            );

            // Set the questionId and answerText parameters
            pst.setInt(1, questionId);
            pst.setString(2, answerText);

            rs = pst.executeQuery();

            // Get the trueness value if available
            if (rs.next()) {
                isTrue = rs.getBoolean("trueness");
            }

        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }

        return isTrue;
    }

    //inserts question to the american question table
    public static int InsertToTable(Connection connection, String strQuestion, String subjectName, String diff) {
        PreparedStatement pst;
        int questionId;
        try {
            if (!Question.isQuestionTextInTable(connection, strQuestion, subjectName)) {
                questionId = Question.insertIntoTable(connection, strQuestion, subjectName, diff);
            } else {
                questionId = -1;
            }

            if (questionId == -1)
                return -1;
            pst = connection.prepareStatement("INSERT INTO AmericanQuestion (questionId) VALUES (?);");
            pst.setInt(1, questionId);
            pst.executeUpdate();
            pst.close();
            return questionId;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //checks if the answer already exists for the question
    public static boolean isAnswerTextInAmericanQuestion(int questionId, String answerText, Connection connection) {
        try (PreparedStatement pst = connection.prepareStatement("SELECT 1 FROM QuestionAnswer qa " +
                "WHERE qa.questionId = ? AND qa.answerText = ?")) {
            pst.setInt(1, questionId);
            pst.setString(2, answerText);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next(); // Returns true if there is at least one result
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }

    // add answer to question
    public static int addAnswerToQuestion(String answerText, int qId, boolean trueness, Connection connection) {
        String sql = "INSERT INTO QuestionAnswer (questionId, answerText, trueness) VALUES (?, ?, ?)";

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

    //returns all answers for american question
    public static String getAmericanQuestionAnswers(Connection connection, int questionId) throws SQLException {
        String str = "Answers:\n";
        PreparedStatement pst = null;
        ResultSet rs = null;
        int i = 1;
        try {
            pst = connection.prepareStatement("SELECT answerText FROM QuestionAnswer WHERE questionId = ?");
            pst.setInt(1, questionId);
            rs = pst.executeQuery();
            while (rs.next()) {
                str += i + ")" + rs.getString("answerText") + "\n";
                i++;
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
        return str + "\n";
    }

}
