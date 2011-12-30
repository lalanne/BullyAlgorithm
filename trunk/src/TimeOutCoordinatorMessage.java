package bully;

public class TimeOutCoordinatorMessage implements Message{
	private String type = "TimeOutCoordinatorMessage";
	private int from;
	
	TimeOutCoordinatorMessage(final int from){
		this.from = from;
	}
	
	public String getType(){
		return type;
	}
	
	public final int getFrom(){
		return from;
	}
}
