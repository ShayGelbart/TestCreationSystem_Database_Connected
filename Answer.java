package testing;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Answer implements Serializable {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;
	private AnswerText answerText;
	private boolean trueness;

	public Answer(AnswerText answerText, boolean trueness) {
		this.answerText = answerText;
		this.trueness = trueness;
	}

	public Answer(Answer other) {
		this.answerText = other.answerText;
		this.trueness = other.trueness;
	}

	public void setTrueness(boolean trueness) {
		this.trueness = trueness;
	}

	// no set for the text because there were no request for it
	public boolean getTrueness() {
		return trueness;
	}

	public AnswerText getAnswer() {
		return answerText;
	}
	
	public int hashCode() {
        return Objects.hash(answerText);
    }
	
	public String toString() {
		return "Answer-" + answerText + "," + trueness;
	}
}
