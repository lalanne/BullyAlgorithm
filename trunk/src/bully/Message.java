package bully;

public abstract class Message {
	protected String type;
	protected int from;
	
	public String getType(){
		return type;
	}
	
	public final int getFrom(){
		return from;
	}
}
