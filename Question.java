package testing;

import java.io.Serializable;
import java.util.Objects;

public abstract class Question implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Difficulty {Easy, Medium, Hard};

	protected Difficulty diff;
	protected String questionText;
	protected static int idCounter = 1;
	protected int id;

	public Question(String questionText) {
		this.questionText = questionText;
		this.id = idCounter++;
	}

	public String getQuestionText() {
		return questionText;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int newId) {
		this.id = newId;
	}
	
	public void setStaticId(int newId) {
		Question.idCounter = newId;
	}
	public Difficulty getDiff() {
		return diff;
	}

	public void setDiff(Difficulty diff) {
		this.diff = diff;
	}
	
	protected String testToString() {
		return questionText;
		
	}
	
    public int hashCode() {
        return Objects.hash(diff, questionText, id);
    }
	
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Question other = (Question) obj;
        return Objects.equals(questionText, other.questionText);
    }
    
	public String toString() {
		return "Id-" + id + "\nQuestion text-" + questionText;
	}
}
