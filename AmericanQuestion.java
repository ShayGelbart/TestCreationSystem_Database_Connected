package testing;

import java.io.Serial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AmericanQuestion extends Question {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;
    private ArrayList<Answer> answersForQuestions = new ArrayList<>();

    public AmericanQuestion(String questionText, Difficulty diff) {
        super(questionText);
        this.diff = diff;
    }

    public AmericanQuestion(AmericanQuestion other) {
        super(other.questionText);
        this.diff = other.diff;

        // Create a new ArrayList for answersForQuestions and copy the contents
        this.answersForQuestions = new ArrayList<>();
        for (Answer answer : other.answersForQuestions) {
            this.answersForQuestions.add(new Answer(answer));
        }
    }

    private void setAnswerTextArray(ArrayList<Answer> answersForQuestions) {
        this.answersForQuestions = answersForQuestions;
    }


    public static int getAnswerCount(Connection connection, int qId) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM QuestionAnswer WHERE questionId = ?")) {
            pst.setInt(1, qId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            int res = rs.getInt(1);
            rs.close();
            pst.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getAnswerByIndex(Connection connection, int questionId, int index) throws SQLException {
        String answerText = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Prepare the SQL query to get the answer by index
            pst = connection.prepareStatement(
                    "SELECT A.answerText FROM QuestionAnswer QA " +
                            "JOIN Answer A ON QA.answerText = A.answerText " +
                            "WHERE QA.questionId = ? ORDER BY A.answerText " +
                            "LIMIT 1 OFFSET ?"
            );

            // Set the questionId and index (offset) parameters
            pst.setInt(1, questionId);
            pst.setInt(2, index - 1);  // Offset starts from 0, so subtract 1 from the index

            rs = pst.executeQuery();

            // Get the answer text if available
            if (rs.next()) {
                answerText = rs.getString("answerText");
            }

        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }

        return answerText;
    }

    public ArrayList<Answer> getAnswersForQuestions() {
        return answersForQuestions;
    }

    public int getNumOfCorrectAnswers() {
        int counter = 0;
        for (Answer answersForQuestion : answersForQuestions)
            if (answersForQuestion.getTrueness())
                counter++;
        return counter;
    }

    public static boolean isAnswerTrue(Connection connection, int questionId, String answerText) throws SQLException {
    boolean isTrue = false;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        // Prepare the SQL query to check if the answer is true or false
        pst = connection.prepareStatement(
            "SELECT A.trueness " +
            "FROM QuestionAnswer QA " +
            "JOIN Answer A ON QA.answerText = A.answerText " +
            "WHERE QA.questionId = ? AND QA.answerText = ?"
        );

        // Set the questionId and answerText parameters
        pst.setInt(1, questionId);
        pst.setString(2, answerText);

        rs = pst.executeQuery();

        // Get the trueness value if available
        if (rs.next()) {
            isTrue = rs.getBoolean("trueness");
        }

    } finally {
        if (rs != null) rs.close();
        if (pst != null) pst.close();
    }

    return isTrue;
}


    public static int getNumOfIncorrectAnswers(Connection connection, int questionId) throws SQLException {
        int incorrectAnswersCount = 0;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            pst = connection.prepareStatement(
                    "SELECT COUNT(*) AS incorrectCount FROM QuestionAnswer QA " +
                            "JOIN Answer A ON QA.answerText = A.answerText " +
                            "WHERE QA.questionId = ? AND A.trueness = FALSE");
            pst.setInt(1, questionId);  // Set the questionId parameter
            rs = pst.executeQuery();

            // Get the count of incorrect answers
            if (rs.next()) {
                incorrectAnswersCount = rs.getInt("incorrectCount");
            }

        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }

        return incorrectAnswersCount;
    }


    public static int InsertToTable(Connection connection, String strQuestion, String diff) {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("INSERT INTO AmericanQuestion (questionText, difficulty) VALUES (?, ?) RETURNING id;");
            pst.setString(1, strQuestion);
            pst.setString(2, diff);
            int result = pst.executeUpdate();
            pst.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // add answer to question
    public static int addAnswerToQuestion(String answerText, int qId, boolean trueness, Connection connection) {
        try (PreparedStatement pst = connection.prepareStatement("INSERT INTO QuestionAnswer VALUES (?, ?, ?)")) {
            pst.setInt(1, qId);
            pst.setString(2, answerText);
            pst.setBoolean(3, trueness);
            int result = pst.executeUpdate();
            pst.close();
            return result;
        } catch (SQLException e) {
            return -1;
        }
    }

//		if (answersForQuestions.size() == 10)
//			return false;
//        for (Answer answersForQuestion : answersForQuestions)
//            if (answersForQuestion.getAnswer().equals(answerText))
//                return false;
//		Answer addedAnswer = new Answer(answerText, trueness);
//		answersForQuestions.add(addedAnswer);
//		setAnswerTextArray(answersForQuestions);
//		return true;


    // delete answer to question via index
    public boolean deleteAnswerFromQuestion(int index) {
        if (index > answersForQuestions.size() || index <= 0) {
            System.out.println("Failed to delete answer");
            return false;
        }
        answersForQuestions.remove(index - 1);
        System.out.println("successfully deleted answer");
        return true;
    }

    // deletes all of a question's answers
    public void deleteAllAnswers() {
        answersForQuestions.clear();
    }


    public String questionWithAnswersToString() {
        String str = diff.name() + ", American question, " + super.toString() + "\n";
        for (Answer answersForQuestion : this.answersForQuestions) {
            str += answersForQuestion.toString() + "\n";
        }
        return str;
    }

    public static String getAmericanQuestionAnswers(Connection connection, int questionId) throws SQLException {
        String str = "Answers:\n";
        PreparedStatement pst = null;
        ResultSet rs = null;
        int i = 1;
        try {
            pst = connection.prepareStatement("SELECT answerText FROM QuestionAnswer WHERE questionId = ?");
            pst.setInt(1, questionId);
            rs = pst.executeQuery();
            while (rs.next()) {
                str += i + ")" + rs.getString("answerText") + "\n";
                i++;
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        }
        return str;
    }

    public String toString() {
        int i = 0;
        StringBuilder str = new StringBuilder(super.toString() + "\n(American question) \n");
        for (Answer answersForQuestion : this.answersForQuestions) {
            str.append((i + 1)).append(")").append(answersForQuestion.toString()).append("\n");
            i++;
        }
        return str.toString();

    }

    public String testToString(int questionId, Connection connection) throws SQLException {
        String str = "(American question)-" + super.testToString();
        str += getAmericanQuestionAnswers(connection, questionId);
        return str;
    }

// functions that are overridden
//	public int getAnswerCount();
//
//	public boolean addAnswerToQuestion(AnswerText answerText, boolean answerIsTrue);
//
//	public boolean deleteAnswerFromQuestion(int indexAnswer);
//
//	public void deleteAllAnswers();
//
//	public Answer getAnswerByIndex(int index);
//
//	public String questionWithAnswersToString();


}
