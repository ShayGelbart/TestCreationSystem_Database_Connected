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
    private static final long serialVersionUID = 1L;
    private String answerText;

    public AnswerText(String answerText) {
        this.answerText = answerText;
    }

    public AnswerText(AnswerText other) {
        this.answerText = other.answerText;
    }


    // no set for the text because there were no request for it
    public String getAnswerText() {
        return answerText;
    }

    public static Boolean InsertToTable(Connection connection, String answer) {
        PreparedStatement pst = null;
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

    public static String getAnswerTextByIndex(int ansIndex, Connection connection) {
        try (PreparedStatement pst = connection.prepareStatement("SELECT * FROM AnswerText LIMIT ? OFFSET ?")) {
            pst.setInt(1, 1);
            pst.setInt(2, ansIndex - 1);
            ResultSet rs = pst.executeQuery();
            rs.next();
            String answerText = rs.getString(1);
            rs.close();
            pst.close();
            return answerText;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int hashCode() {
        return Objects.hash(answerText);
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        AnswerText other = (AnswerText) obj;
        return Objects.equals(answerText, other.answerText);
    }

    public String toString() {
        return answerText;
    }
}
