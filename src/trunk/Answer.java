package bully;

public class Answer implements Message {
	private String type = "Answer";
	private int from;
	
	Answer(final int from){
		this.from = from;
	}
	
	public String getType(){
		return type;
	}
	
	public final int getFrom(){
		return from;
	}
}
