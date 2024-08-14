package testing;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface Examable {

    boolean createExam(String subjectName, int numOfQuestions, Connection connection) throws SQLException, IOException;

}
