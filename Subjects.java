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
    private static final long serialVersionUID = 1L;
    private ArrayList<Pool> pools;

    public Subjects() {
        this.pools = new ArrayList<>();
    }

    public ArrayList<Pool> getPools() {
        return pools;
    }

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
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return subjectName;
    }

    public void setPools(ArrayList<Pool> pools) {
        this.pools = pools;
    }

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

    // adds a test to the data base
    public static boolean addPoolToArray(String subject, Connection connection) throws SQLException {
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


    // prints an entire test based on index
    public String toStringSubjectByIndex(int index) {
        return pools.get(index - 1).toString();
    }

    @SuppressWarnings("unchecked")
    public void readFromBinaryFile() throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream inFile = new ObjectInputStream(new FileInputStream("Subjects.dat"));
        pools = (ArrayList<Pool>) inFile.readObject();
        inFile.close();
    }

    public void writeToBinaryFile() throws FileNotFoundException, IOException {
        ObjectOutputStream outFile = new ObjectOutputStream(new FileOutputStream("Subjects.dat"));
        outFile.writeObject(pools);
        outFile.close();
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

    public int hashCode() {
        return Objects.hash(pools);
    }

    // prints the entire data base's tests
    public String toString() {
        String str = "The entire data base:\n";
        for (Pool pool : this.pools) {
            str += pool.toString() + "\n";
        }
        return str;
    }
}
