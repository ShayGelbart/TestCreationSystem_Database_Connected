package testing;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public interface Examable {

    static boolean createExam(String subjectName, int numOfQuestions, Connection connection, Scanner sc) throws SQLException, IOException {
        return false;
    }

}
