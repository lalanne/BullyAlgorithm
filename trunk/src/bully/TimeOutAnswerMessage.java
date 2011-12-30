package bully;

public class TimeOutAnswerMessage implements Message{
	private String type = "TimeOutAnswerMessage";
	private int from;
	
	TimeOutAnswerMessage(final int from){
		this.from = from;
	}
	
	public String getType(){
		return type;
	}
	
	public final int getFrom(){
		return from;
	}
}
