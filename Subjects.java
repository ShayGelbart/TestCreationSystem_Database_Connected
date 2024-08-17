package testing;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Subjects implements Serializable {
    /**
     *
     */
    @Serial
    //returns subject at index
    public static String getPoolsAtIndex(int index, Connection connection) {
        String subjectName = null;
        PreparedStatement pst = null;
        try {
            // Step 1: Get the primary key (subjectName) of the row at the specified index
            pst = connection.prepareStatement("SELECT subjectName FROM Pool LIMIT 1 OFFSET ?");
            pst.setInt(1, index - 1);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                subjectName = rs.getString("subjectName");
                rs.close();
                pst.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjectName;
    }

    //returns amount of subjects
    public static int getAmountOfPools(Connection connection) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM Pool")) {
            ResultSet rs = pst.executeQuery();
            rs.next();
            int result = rs.getInt(1);
            rs.close();
            pst.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    //checks if subject is in the database
    public static boolean isSubjectInTable(Connection connection, String subjectName) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            // Prepare the SQL statement to check for the existence of the subjectName
            pst = connection.prepareStatement("SELECT 1 FROM Pool WHERE subjectName = ?");
            pst.setString(1, subjectName);

            // Execute the query
            rs = pst.executeQuery();

            // If the result set has at least one row, the subjectName exists
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


    // adds a test to the data base
    public static boolean insertToTable(String subject, Connection connection) throws SQLException {
        if (isSubjectInTable(connection, subject)) {
            return false;
        }
        PreparedStatement pst = null;
        boolean check;
        try {
            pst = connection.prepareStatement("INSERT INTO Pool VALUES (?)");
            pst.setString(1, subject);
            check = pst.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        } finally {
            if (pst != null)
                pst.close();
        }
        return check;
    }

    // deleted a question from the data base
    public static boolean deletePoolFromArray(int index, Connection connection) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("SELECT subjectName FROM Pool LIMIT 1 OFFSET ?");
            pst.setInt(1, index - 1);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String subjectName = rs.getString("subjectName");
                rs.close();
                pst.close();

                pst = connection.prepareStatement("DELETE FROM Pool WHERE subjectName = ?");
                pst.setString(1, subjectName);
                boolean result = pst.executeUpdate() > 0;
                pst.close();
                return result;
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

    // prints the names of every subject which has a test
    public static String toStringSubjectNames(Connection connection) throws SQLException {
        String str = "Names of every subject: \n";
        int i = 0;
        PreparedStatement pst;
        try {
            pst = connection.prepareStatement("SELECT subjectName FROM Pool");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                str += (i + 1) + ")" + rs.getString("subjectName") + "\n";
                i++;
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return str;
    }

}
