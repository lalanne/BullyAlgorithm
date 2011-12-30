package bully;

public class Coordinator implements Message{
	private final String type = "Coordinator";
	private int from;
	
	Coordinator(final int from){
		this.from = from;
	}
	
	public String getType(){
		return type;
	}
	
	public final int getFrom(){
		return from;
	}
}
