package testing;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
            return pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String testToString() {
        return diff.name() + ", open question, " + super.toString();
    }

    public String toString() {
        return diff.name() + ", open question, " + super.toString() + "\nAnswer-" + schoolSolution + "\n";
    }

    // Overridden functions

    @Override
    public int getAnswerCount() {
        return 0;
    }

    @Override
    public boolean addAnswerToQuestion(AnswerText answerText, boolean answerIsTrue) {
        return false;
    }

    @Override
    public boolean deleteAnswerFromQuestion(int indexAnswer) {
        return false;
    }

    @Override
    public void deleteAllAnswers() {
    }

    @Override
    public Answer getAnswerByIndex(int index) {
        return null;
    }


    @Override
    public String questionWithAnswersToString() {
        return super.testToString() + "\nAnswer-" + schoolSolution + "\n";
    }
}
