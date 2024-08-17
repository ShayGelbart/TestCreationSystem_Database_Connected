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

    public static int getQuestionIdByQuestionText(Connection connection, String questionText) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            // Prepare the SQL statement to retrieve the questionId based on questionText
            pst = connection.prepareStatement("SELECT questionId FROM Question WHERE questionText = ?");
            pst.setString(1, questionText);

            // Execute the query
            rs = pst.executeQuery();

            // If the questionText is found, return the questionId
            if (rs.next()) {
                return rs.getInt("questionId");
            } else {
                // Return -1 if the questionText does not exist
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            // Clean up resources
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }


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

    public static String getQuestionTable(Connection connection) throws SQLException {
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

