package bully;

public class Election implements Message {
	private String type = "Election";
	private int from;
	
	Election(final int from){
		this.from = from;
	}
	
	public String getType(){
		return type;
	}
	
	public final int getFrom(){
		return from;
	}
}
