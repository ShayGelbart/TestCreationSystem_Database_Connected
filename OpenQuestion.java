package testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OpenQuestion {
    /**
     *
     */

    // returns int so the Id will be passed to another function later on
    public static int InsertToTable(Connection connection, String strQuestion, String subjectName, String diff, String answer) throws SQLException {
        PreparedStatement pst = null;
        int questionId;
        try {
            if (!Question.isQuestionTextInTable(connection, strQuestion, subjectName)) {
                questionId = Question.insertIntoTable(connection, strQuestion, subjectName, diff);
            } else {
                questionId = -1;
            }
            if (questionId == -1)
                return -1;

            pst = connection.prepareStatement("INSERT INTO OpenQuestion (questionId, schoolSolution) VALUES (?, ?);");
            pst.setInt(1, questionId);
            pst.setString(2, answer);
            pst.executeUpdate();
            return questionId;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pst != null)
                pst.close();
        }
    }

    //returns answer to the open question
    public static String getOpenQuestionSolution(int questionId, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT schoolSolution FROM OpenQuestion WHERE questionId = ?");
            pst.setInt(1, questionId);
            rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("schoolSolution");
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
        return "";
    }

    //returns open question and its difficulty
    public static String getOpenQuestionTextAndDiff(int questionId, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT questionText, difficulty FROM Question WHERE questionId = ?");
            pst.setInt(1, questionId);
            rs = pst.executeQuery();
            if (rs.next()) {
                return "Open question," + rs.getString("difficulty") + ": " + rs.getString("questionText") + "\n";
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
        return "";
    }

}
