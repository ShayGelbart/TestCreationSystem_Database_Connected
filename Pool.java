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

    public static int getQuestionArrayAtIndex(int qIndex, Connection connection, String subName) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement("SELECT * FROM QuestionsPool WHERE subjectName LIKE ? LIMIT 1 OFFSET ? RETURNING questionId");
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

    //
//    public String getSubName() {
//        return subName;
//    }
//
//    public void setSubName(String subName) {
//        this.subName = subName;
//    }
//
//    public int hashCode() {
//        return Objects.hash(subName);
//    }

    public static int getAmountOfQuestionsInSubjectPool(Connection connection, String subName) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM Question WHERE subjectName LIKE ?")) {
            pst.setString(1, subName);
            ResultSet rs = pst.executeQuery();
            int res = 0;
            if (rs.next())
                res =  rs.getInt(1);
            rs.close();
            return res;
        } catch (SQLException e) {
            return -1;
        }
    }

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


    // add answer to answer pool
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


    public static int countAmericanQuestionsWithMoreThanFourAnswers(Connection connection, String subName) throws SQLException {
        PreparedStatement pst;
        ResultSet rs;
        try {
            pst = connection.prepareStatement("SELECT COUNT(*) FROM QuestionsPool QP" +
                    "JOIN QuestionAnswer QA ON QP.questionId = QA.questionId" +
                    "WHERE QP.subjectName LIKE ?" +
                    "GROUP BY QP.questionId" +
                    "HAVING COUNT(QA.answerText) >= 4");
            pst.setString(1, subName);
            rs = pst.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    // delete question to answer pool
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

//        if (index <= answerTextArray.size() && index > 0) {
//            questionArray.remove(index - 1);
//            return true;
//        } else
//            return false;

//    public void deleteAllAnswersFromAllQuestions() {
//        for (Question question : questionArray)
//            question.deleteAllAnswers();
//    }

    // add question to pool
    public static boolean addQuestionToPool(Connection connection, int questionId, String subject) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("INSERT INTO QuestionsPool VALUES (?, ?)")) {
            pst.setInt(1, questionId);
            pst.setString(2, subject);
            boolean result = pst.executeUpdate() > 0;
            pst.close();
            return result;
        } catch (SQLException e) {
            return false;
        }


        //        for (Question question : questionArray)
//            if (q.getQuestionText().equals(question.getQuestionText()))
//                return false;
//        questionArray.add(q);
//        return true;
    }

    // add answer to question based on its index from the pool
//        public boolean addAnswerToAmericanQuestionByIndex ( int indexQuestion, int indexAnswer, boolean answerIsTrue){
//            if (questionArray.get(indexQuestion - 1).getAnswerCount() >= 10)
//                return false;
//            questionArray.get(indexQuestion - 1).addAnswerToQuestion(answerTextArray.get(indexAnswer - 1), answerIsTrue);
//            return true;
//        }

    // delete answer from question based on its index from the pool
//        public boolean deleteAnswerFromQuestionByIndex ( int indexQuestion, int indexAnswer) {
//            if (questionArray.get(indexQuestion - 1).getAnswerCount() == 0)
//                return false;
//            questionArray.get(indexQuestion - 1).deleteAnswerFromQuestion(indexAnswer);
//            return true;
//        }
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (obj == null || getClass() != obj.getClass()) return false;
//        Pool other = (Pool) obj;
//        return Objects.equals(subName);
//    }

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
                        str += "Open Question: " + questionText + "\n";
                        str += OpenQuestion.getOpenQuestionSolution(questionId, connection);
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
//    public String questionPoolToString() {
//        int i = 0;
//        String str = "Here is the question pool:\n\n";
//        for (Question q : this.questionArray) {
//            if (q instanceof OpenQuestion)
//                str += (i + 1) + ")" + q.toString() + "\n";
//            else // American question
//                str += (i + 1) + ")" + q.questionWithAnswersToString() + "\n";
//            i++;
//        }
//        return str;
//    }


    public static String questionsSeperatedFromAnswers(Connection connection, String subjectName) throws
            SQLException {
        return questionPoolToString(subjectName, connection) + "\n" + answerTextPoolToString(connection, subjectName);
    }

    // prints the questions with their answers
//    public String toString() {
//        int i = 0;
//        String str = "Here is the questions with their answers:" + "\n";
//        for (Question q : this.questionArray) {
//            if (q instanceof AmericanQuestion)
//                str += (i + 1) + ")" + q.questionWithAnswersToString() + "\n";
//            else
//                str += (i + 1) + ")" + q.toString() + "\n";
//            i++;
//        }
//        return str;
//    }
}
