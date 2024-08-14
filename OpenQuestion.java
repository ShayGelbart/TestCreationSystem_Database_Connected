package testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OpenQuestion extends Question {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private AnswerText schoolSolution;

    public OpenQuestion(String questionText, AnswerText schoolSolution, Difficulty diff) {
        super(questionText);
        this.diff = diff;
        this.schoolSolution = schoolSolution;

    }

    public void setSchoolSolution(AnswerText schoolSolution) {
        this.schoolSolution = schoolSolution;
    }

    public AnswerText getSchoolSolution() {
        return schoolSolution;
    }

    // returns int so the Id will be passed to another function later on
    public static int InsertToTable(Connection connection, String strQuestion, String diff, String answer) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("INSERT INTO OpenQuestion (schoolSolution, questionText, difficulty) VALUES (?, ?, ?) RETURNING id;");
            pst.setString(1, answer);
            pst.setString(2, strQuestion);
            pst.setString(3, diff);
            int result = pst.executeUpdate();
            pst.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getOpenQuestionSolution(Connection connection, int questionId) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT schoolSolution FROM OpenQuestion WHERE id = ?");
            pst.setInt(1, questionId);
            rs = pst.executeQuery();
            if (rs.next()) {
                return "Solution: " + rs.getString("schoolSolution") + "\n";
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
        return "";
    }

    public String testToString() {
        return diff.name() + ", open question, " + super.toString();
    }

    public String toString() {
        return diff.name() + ", open question, " + super.toString() + "\nAnswer-" + schoolSolution + "\n";
    }

    public String questionWithAnswersToString() {
        return super.testToString() + "\nAnswer-" + schoolSolution + "\n";
    }
}
