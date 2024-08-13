package testing;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Answer implements Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;
    private AnswerText answerText;
    private boolean trueness;

    public Answer(AnswerText answerText, boolean trueness) {
        this.answerText = answerText;
        this.trueness = trueness;
    }

    public Answer(Answer other) {
        this.answerText = other.answerText;
        this.trueness = other.trueness;
    }

    public void setTrueness(boolean trueness) {
        this.trueness = trueness;
    }

    // no set for the text because there were no request for it
    public boolean getTrueness() {
        return trueness;
    }

    public AnswerText getAnswer() {
        return answerText;
    }

    public static String insertToTableByIndex(int ansIndex, boolean isTrue, Connection connection) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("SELECT * FROM AnswerText LIMIT ? OFFSET ?")) {
            pst.setInt(1, 1);
            pst.setInt(2, ansIndex - 1);
            ResultSet rs = pst.executeQuery();
            rs.next();
            String answerText = rs.getString(1);
            rs.close();
            pst.close();
            if (insertToTable(answerText, isTrue, connection))
                return answerText;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean insertToTable(String answerText, boolean isTrue, Connection connection) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("INSERT INTO Answer VALUES (?, ?)")) {
            pst.setString(1, answerText);
            pst.setBoolean(2, isTrue);
            pst.executeUpdate();
            pst.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(answerText);
    }

    public String toString() {
        return "Answer-" + answerText + "," + trueness;
    }
}
