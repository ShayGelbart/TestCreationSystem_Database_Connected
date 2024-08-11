package testing;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Subjects implements Serializable{
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private ArrayList<Actions> pools;

	public Subjects() {
		this.pools = new ArrayList<>();
	}

	public ArrayList<Actions> getPools() {
		return pools;
	}

	public Actions getPoolsAtIndex(int index) {
		return pools.get(index - 1);
	}

	public void setPools(ArrayList<Actions> pools) {
		this.pools = pools;
	}
	
	public int hashCode() {
        return Objects.hash(pools);
    }
	
	// adds a test to the data base
	public boolean addPoolToArray(Actions a) {
        for (Actions pool : pools)
            if (pool.getSubName().equals(a.getSubName()))
                return false;
		pools.add(a);
		return true;
	}

	// deleted a question from the data base
	public void deletePoolFromArray(int index) {
		pools.remove(index - 1);
	}
	
	// prints an entire test based on index
		public String toStringSubjectByIndex(int index) {
			return pools.get(index - 1).toString();
		}

	@SuppressWarnings("unchecked")
	public void readFromBinaryFile() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream inFile = new ObjectInputStream(new FileInputStream("Subjects.dat"));
		pools = (ArrayList<Actions>) inFile.readObject();
		inFile.close();
	}
	
	public void writeToBinaryFile() throws FileNotFoundException, IOException {
		ObjectOutputStream outFile = new ObjectOutputStream(new FileOutputStream("Subjects.dat"));
		outFile.writeObject(pools);
		outFile.close();
	}
	
	// prints the names of every subject which has a test
	public String toStringSubjectNames() {
		String str = "Names of every subject: \n";
		int i = 0;
        for (Actions pool : this.pools) {
            str += (i + 1) + ")" + pool.getSubName() + "\n";
            i++;
        }
		return str;
	}

	// prints the entire data base's tests
	public String toString() {
		String str = "The entire data base:\n";
        for (Actions pool : this.pools) {
            str += pool.toString() + "\n";
        }
		return str;
	}
}
