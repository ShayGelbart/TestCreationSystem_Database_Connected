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
    public static int InsertToTable(Connection connection, String strQuestion, String diff, String answer) throws SQLException {
        PreparedStatement pst = null;
        int questionId;
        try {
            if (!Question.isQuestionTextInTable(connection, strQuestion)) {
                questionId = Question.insertIntoTable(connection, strQuestion, diff);
            } else {
                questionId = Question.getQuestionIdByQuestionText(connection, strQuestion);
            }
            if (questionId == -1)
                return -1;

            pst = connection.prepareStatement("INSERT INTO OpenQuestion (questionId, schoolSolution) VALUES (?, ?);");
            pst.setInt(1, questionId);
            pst.setString(2, answer);
            pst.executeQuery();
            return questionId;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (pst != null)
                pst.close();
        }
    }

    public static String getOpenQuestionSolution(int questionId, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT schoolSolution FROM OpenQuestion WHERE questionId = ?");
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
