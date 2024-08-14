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
    private static final long serialVersionUID = 1L;

    public enum Difficulty {Easy, Medium, Hard}

    ;

    protected Difficulty diff;
    protected String questionText;
    protected static int idCounter = 1;
    protected int id;

    public Question(String questionText) {
        this.questionText = questionText;
        this.id = idCounter++;
    }

    public String getQuestionText() {
        return questionText;
    }

    public int getId() {
        return id;
    }

    public void setId(int newId) {
        this.id = newId;
    }

    public void setStaticId(int newId) {
        Question.idCounter = newId;
    }

    public Difficulty getDiff() {
        return diff;
    }

    public void setDiff(Difficulty diff) {
        this.diff = diff;
    }

    public static int insertIntoTable(Connection connection, String questionText, String diff) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("INSERT INTO Question (questionText, difficulty) VALUES (?, CAST(? AS difficulty)) RETURNING questionId")) {
            pst.setString(1, questionText);
            pst.setString(2, diff);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("questionId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

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

    protected String testToString() {
        return questionText;

    }

    public int hashCode() {
        return Objects.hash(diff, questionText, id);
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Question other = (Question) obj;
        return Objects.equals(questionText, other.questionText);
    }

    public String toString() {
        return "Id-" + id + "\nQuestion text-" + questionText;
    }
}
