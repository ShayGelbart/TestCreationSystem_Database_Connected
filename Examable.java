package testing;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface Examable {

    static boolean createExam(String subjectName, int numOfQuestions, Connection connection) throws SQLException, IOException {
        return false;
    }

}
