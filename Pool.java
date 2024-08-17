package testing;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Pool implements Serializable {
    /**
     *
     */
    @Serial
    //returns answers for the specific subject
    public static String getAnswerTextArrayAtIndex(Connection connection, String subName, int index) {
        PreparedStatement pst;
        try {
            pst = connection.prepareStatement("SELECT * FROM AnswersPool WHERE subjectName LIKE ? LIMIT 1 OFFSET ?");
            pst.setString(1, subName);
            pst.setInt(2, index - 1);
            ResultSet rs = pst.executeQuery();
            if (rs.next())
                return rs.getString("answerText");
            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    //returns the id if the question by index of the row and subject name
    public static int getQuestionArrayAtIndex(int qIndex, Connection connection, String subName) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT * FROM Question WHERE subjectName LIKE ? LIMIT 1 OFFSET ?");
            pst.setString(1, subName);
            pst.setInt(2, qIndex - 1);
            rs = pst.executeQuery();
            if (rs.next())
                return rs.getInt("questionId");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (rs != null)
                rs.close();
            if (pst != null)
                pst.close();
        }
        return 0;

    }
    //returns amount of questions in the subject pool
    public static int getAmountOfQuestionsInSubjectPool(Connection connection, String subName) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM Question WHERE subjectName LIKE ?")) {
            pst.setString(1, subName);
            ResultSet rs = pst.executeQuery();
            int res = 0;
            if (rs.next())
                res = rs.getInt(1);
            rs.close();
            return res;
        } catch (SQLException e) {
            return -1;
        }
    }
    //returns amount of answers in the subject pool
    public static int getAmountOfAnswersInSubjectPool(Connection connection, String subName) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM AnswersPool WHERE subjectName LIKE ?")) {
            pst.setString(1, subName);
            ResultSet rs = pst.executeQuery();
            int check = 0;
            if (rs.next())
                check = rs.getInt(1);
            rs.close();
            pst.close();
            return check;
        } catch (SQLException e) {
            return -1;
        }
    }
    //checks if answer in the subject pool
    public static boolean isAnswerInSubjectPool(Connection connection, String answerText, String subjectName) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            // Prepare the SQL statement to check if the answerText is in the pool of the given subject
            pst = connection.prepareStatement("SELECT 1 FROM AnswersPool WHERE answerText = ? AND subjectName = ?");
            pst.setString(1, answerText);
            pst.setString(2, subjectName);

            // Execute the query
            rs = pst.executeQuery();

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


    // add answer to answers pool
    public static int addAnswerTextToPool(String answerStr, String subjectName, Connection connection) throws SQLException {
        if (isAnswerInSubjectPool(connection, answerStr, subjectName)) {
            return 0;
        }

        try (PreparedStatement pst = connection.prepareStatement("INSERT INTO AnswersPool VALUES (?, ?)")) {
            pst.setString(1, answerStr);
            pst.setString(2, subjectName);
            return pst.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
    }

    // delete question from answers pool
    public static boolean deleteQuestionFromArray(int index, String subjectName, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("SELECT questionId FROM Question WHERE subjectName = ? LIMIT 1 OFFSET ?");
            pst.setString(1, subjectName);
            pst.setInt(2, index - 1);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int questionId = rs.getInt("questionId");
                rs.close();
                pst.close();

                pst = connection.prepareStatement("DELETE FROM Question WHERE questionId = ?");
                pst.setInt(1, questionId);
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pst != null) {
                pst.close();
            }
        }
        return false; // Return false if no row was deleted
    }

    public static boolean deleteAnswerByIndexFromPool(Connection connection, String subjectName, int answerIndex) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            String selectSql = "SELECT answerText FROM AnswersPool WHERE subjectName = ? LIMIT 1 OFFSET ?";
            pst = connection.prepareStatement(selectSql);
            pst.setString(1, subjectName);
            pst.setInt(2, answerIndex - 1);

            rs = pst.executeQuery();
            if (!rs.next()) {
                System.out.println("No answer found at the specified index.");
                return false;
            }

            String answerText = rs.getString("answerText");

            String deleteSql = "DELETE FROM AnswersPool WHERE answerText = ? AND subjectName = ?";
            pst = connection.prepareStatement(deleteSql);
            pst.setString(1, answerText);
            pst.setString(2, subjectName);

            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }
    //checks what type of question
    public static boolean isQuestionType(Connection connection, int questionId, String tableName) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT 1 FROM " + tableName + " WHERE questionId = ?");
            pst.setInt(1, questionId);
            rs = pst.executeQuery();
            return rs.next(); // If there's a result, it means the question exists in that table
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
    }

    // prints the answers text from the pool
    public static String answerTextPoolToString(Connection connection, String subName) throws SQLException {
        int i = 0;
        String str = "Here is the answer pool:" + "\n";
        PreparedStatement pst;
        try {
            pst = connection.prepareStatement("SELECT answerText FROM AnswersPool WHERE subjectName LIKE ?");
            pst.setString(1, subName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                str += (i + 1) + ")" + rs.getString("answerText") + "\n";
                i++;
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        if (i == 0)
            return "There are no answers in the pool";
        return str;
    }

    // prints the questions text from the pool
    public static String questionPoolToString(String subjectName, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        int i = 0;
        String str = "Here is the question pool:" + "\n";
        try {
            pst = connection.prepareStatement("SELECT questionId FROM Question WHERE subjectName = ?");
            pst.setString(1, subjectName);
            rs = pst.executeQuery();

            while (rs.next()) {
                int questionId = rs.getInt("questionId");

                // Retrieve the question from the Question table
                PreparedStatement pstQuestion = connection.prepareStatement("SELECT * FROM Question WHERE questionId = ?");
                pstQuestion.setInt(1, questionId);
                ResultSet rsQuestion = pstQuestion.executeQuery();

                if (rsQuestion.next()) {
                    String questionText = rsQuestion.getString("questionText");
                    str += (i + 1) + ")";
                    // Check if it's an OpenQuestion or AmericanQuestion
                    if (isQuestionType(connection, questionId, "OpenQuestion")) {
                        str += "Open Question: " + questionText + "\nSolution: ";
                        str += OpenQuestion.getOpenQuestionSolution(questionId, connection) + "\n";
                    } else {
                        str += "American Question: " + questionText + "\n";
                        str += AmericanQuestion.getAmericanQuestionAnswers(connection, questionId);
                    }
                }
                i++;
                pstQuestion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }

        if (i == 0)
            return "There are no questions in the pool\n";
        return str;
    }

    public static String questionsSeperatedFromAnswers(Connection connection, String subjectName) throws
            SQLException {
        return questionPoolToString(subjectName, connection) + "\n" + answerTextPoolToString(connection, subjectName);
    }

    public static String getPoolTable(Connection connection)  {
        StringBuilder result = new StringBuilder("Pool Table:\n");
        String query = "SELECT * FROM Pool";
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String subjectName = rs.getString("subjectName");
                result.append("SubjectName: ").append(subjectName).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
