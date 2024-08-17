package testing;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class AnswerText implements Serializable {
    /**
     *
     */
    @Serial

    //checks if answer is in the database
    public static boolean isAnswerTextInTable(Connection connection, String answerText) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            // Prepare the SQL statement to check for the existence of the answerText
            pst = connection.prepareStatement("SELECT 1 FROM AnswerText WHERE answerText = ?");
            pst.setString(1, answerText);

            // Execute the query
            rs = pst.executeQuery();

            // If the result set has at least one row, the answerText exists
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

    //inserts answer to table
    public static Boolean InsertToTable(Connection connection, String answer) {
        PreparedStatement pst;
        try {
            pst = connection.prepareStatement("INSERT INTO Answertext (answerText) VALUES (?)");
            pst.setString(1, answer);
            boolean result = pst.executeUpdate() >= 0;
            pst.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //returns answer by the index of the row
    public static String getAnswerTextByIndex(int ansIndex, String subjectName, Connection connection) {
        try (PreparedStatement pst = connection.prepareStatement("SELECT answerText FROM AnswersPool WHERE subjectName = ? LIMIT ? OFFSET ?")) {
            pst.setString(1, subjectName);
            pst.setInt(2, 1);
            pst.setInt(3, ansIndex - 1);
            ResultSet rs = pst.executeQuery();
            String answerText = "";
            if (rs.next())
                answerText = rs.getString("answerText");
            rs.close();
            pst.close();
            return answerText;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //return all the info of the answers tables
    public static String getAnswerTextTable(Connection connection)  {
        StringBuilder result = new StringBuilder("AnswerText Table:\n");
        String query = "SELECT * FROM AnswerText";
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String answerText = rs.getString("answerText");
                result.append("AnswerText: ").append(answerText).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}