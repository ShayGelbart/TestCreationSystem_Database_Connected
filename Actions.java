package testing;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Actions implements Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;
    private ArrayList<AnswerText> answerTextArray;
    private ArrayList<Question> questionArray;
    private String subName;

    public Actions(String subName) {
        this.answerTextArray = new ArrayList<>();
        this.questionArray = new ArrayList<>();
        this.subName = subName;
    }

    public Actions(Actions other) {
        this.answerTextArray = new ArrayList<>(other.answerTextArray); // Perform a deep copy of answerTextArray
        this.questionArray = new ArrayList<>(other.questionArray); // Perform a deep copy of questionArray
        this.subName = other.subName;
    }

    public ArrayList<AnswerText> getAnswerTextArray() {
        return answerTextArray;
    }

    public ArrayList<Question> getQuestionArray() {
        return questionArray;
    }

    public void setQuestionArray(ArrayList<Question> questionArray) {
        this.questionArray = questionArray;
    }

    public void setAnswerTextArray(ArrayList<AnswerText> answerTextArray) {
        this.answerTextArray = answerTextArray;
    }

    public Question getQuestionArrayAtIndex(int index) {
        return questionArray.get(index - 1);
    }

    public static String getAnswerTextArrayAtIndex(Connection connection, String subName, int index) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("SELECT * FROM AnswersPool WHERE subjectName LIKE ? LIMIT 1 OFFSET ?");
            pst.setString(1, subName);
            pst.setInt(2, index - 1);
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getString("answerText");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public int hashCode() {
        return Objects.hash(subName);
    }

    public static int getAmountOfQuestionsInSubjectPool(Connection connection, String subName) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM QuestionsPool WHERE subjectName LIKE ?")) {
            pst.setString(1, subName);
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            return -1;
        }
    }

    public static int getAmountOfAnswersInSubjectPool(Connection connection, String subName) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM AnswersPool WHERE subjectName LIKE ?")) {
            pst.setString(1, subName);
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            return -1;
        }
    }


    // add answer to answer pool
    public static int addAnswerTextToPool(String answerStr, String subName, Connection connection) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("INSERT INTO AnswersPool VALUES (?, ?)")) {
            pst.setString(1, answerStr);
            pst.setString(2, subName);
            return pst.executeUpdate();
        } catch (SQLException e) {
            return -1;
        }
    }

    public int countAmericanQuestionsWithMoreThanFourAnswers() {
        int count = 0;
        for (Question question : this.questionArray) {
            if (question instanceof AmericanQuestion) {
                AmericanQuestion americanQuestion = (AmericanQuestion) question;
                if (americanQuestion.getAnswerCount() >= 4) {
                    count++;
                }
            }
        }
        return count;
    }


    // delete question to answer pool
    public boolean deleteQuestionFromArray(int index) {
        if (index <= answerTextArray.size() && index > 0) {
            questionArray.remove(index - 1);
            return true;
        } else
            return false;
    }

    public void deleteAllAnswersFromAllQuestions() {
        for (Question question : questionArray)
            question.deleteAllAnswers();
    }

    // add question to pool
    public static boolean addQuestionToPool(Connection connection, int questionId, String subject) {
        try (PreparedStatement pst = connection.prepareStatement("INSERT INTO QuestionsPool VALUES (?, ?)")) {
            pst.setInt(1, questionId);
            pst.setString(2, subject);
            return pst.executeUpdate() > 0;
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
    public boolean addAnswerToAmericanQuestionByIndex(int indexQuestion, int indexAnswer, boolean answerIsTrue) {
        if (questionArray.get(indexQuestion - 1).getAnswerCount() >= 10)
            return false;
        questionArray.get(indexQuestion - 1).addAnswerToQuestion(answerTextArray.get(indexAnswer - 1), answerIsTrue);
        return true;
    }

    // delete answer from question based on its index from the pool
    public boolean deleteAnswerFromQuestionByIndex(int indexQuestion, int indexAnswer) {
        if (questionArray.get(indexQuestion - 1).getAnswerCount() == 0)
            return false;
        questionArray.get(indexQuestion - 1).deleteAnswerFromQuestion(indexAnswer);
        return true;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Actions other = (Actions) obj;
        return Objects.equals(subName, other.subName);
    }

    // prints the answers text from the pool
    public static String answerTextToString(Connection connection, String subName) throws SQLException {
        int i = 0;
        String str = "Here is the answer pool:" + "\n";
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("SELECT * FROM AnswersPool WHERE subjectName LIKE ?");
            pst.setString(1, subName);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                str += (i + 1) + ")" + rs.getString("answerText");
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

        if (i == 0)
            return "There are no answers in the pool";
        return str;
    }

    // prints the questions text from the pool
    public String questionArrayToString() {
        int i = 0;
        String str = "Here is the question pool:\n\n";
        for (Question q : this.questionArray) {
            if (q instanceof OpenQuestion)
                str += (i + 1) + ")" + q.toString() + "\n";
            else // American question
                str += (i + 1) + ")" + q.questionWithAnswersToString() + "\n";
            i++;
        }
        return str;
    }

    public String questionsSeperatedFromAnswers() {
        return questionArrayToString() + "\n" + answerTextToString();
    }

    // prints the questions with their answers
    public String toString() {
        int i = 0;
        String str = "Here is the questions with their answers:" + "\n";
        for (Question q : this.questionArray) {
            if (q instanceof AmericanQuestion)
                str += (i + 1) + ")" + q.questionWithAnswersToString() + "\n";
            else
                str += (i + 1) + ")" + q.toString() + "\n";
            i++;
        }
        return str;
    }
}
