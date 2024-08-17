package testing;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class Question implements Serializable {

    /**
     *
     */
//    //returns id of the question by the text
//    public static int getQuestionIdByQuestionText(Connection connection, String questionText) throws SQLException {
//        PreparedStatement pst = null;
//        ResultSet rs = null;
//        try {
//            // Prepare the SQL statement to retrieve the questionId based on questionText
//            pst = connection.prepareStatement("SELECT questionId FROM Question WHERE questionText = ?");
//            pst.setString(1, questionText);
//
//            // Execute the query
//            rs = pst.executeQuery();
//
//            // If the questionText is found, return the questionId
//            if (rs.next()) {
//                return rs.getInt("questionId");
//            } else {
//                // Return -1 if the questionText does not exist
//                return -1;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return -1;
//        } finally {
//            // Clean up resources
//            if (rs != null) rs.close();
//            if (pst != null) pst.close();
//        }
//    }

    //checks if there's a question with the same text in the subject
    public static boolean isQuestionTextInTable(Connection connection, String questionText, String subjectName) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            // Prepare the SQL statement to check for the existence of the questionText
            pst = connection.prepareStatement("SELECT 1 FROM Question WHERE questionText = ? AND subjectName = ?");
            pst.setString(1, questionText);
            pst.setString(2, subjectName);
            // Execute the query
            rs = pst.executeQuery();

            // If the result set has at least one row, the questionText exists
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Clean up resources
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }

    //inserts a question into the database
    public static int insertIntoTable(Connection connection, String questionText, String subjectName, String diff) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("INSERT INTO Question (questionText, SubjectName, difficulty) VALUES (?, ?, CAST(? AS difficulty)) RETURNING questionId")) {
            pst.setString(1, questionText);
            pst.setString(2, subjectName);
            pst.setString(3, diff);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("questionId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //returns string of all the difficulties
    public static String[] printDifficulty(Connection connection) {
        String str = "Enter the difficulty:\n";
        try (PreparedStatement pst = connection.prepareStatement("SELECT unnest(enum_range(NULL::difficulty))")) {
            ResultSet rs = pst.executeQuery();

            int i = 0;
            String[] options = new String[4]; // Assuming you have 3 difficulty levels
            while (rs.next()) {
                options[i] = rs.getString(1);
                str += i + ". " + options[i] + "\n";
                i++;
            }
            rs.close();
            pst.close();
            options[3] = str;
            return options;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getQuestionTable(Connection connection) {
        StringBuilder result = new StringBuilder("Question Table:\n");
        String query = "SELECT * FROM Question";
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int questionId = rs.getInt("questionId");
                String questionText = rs.getString("questionText");
                String subjectName = rs.getString("subjectName");
                String difficulty = rs.getString("difficulty");
                result.append("QuestionID: ").append(questionId).append(", QuestionText: ")
                        .append(questionText).append(", SubjectName: ").append(subjectName)
                        .append(", Difficulty: ").append(difficulty).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}

