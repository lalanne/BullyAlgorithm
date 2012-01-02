package bully;

public class Answer extends Message {
	
	Answer(final int from){
		this.type = "Answer";
		this.from = from;
	}
}
