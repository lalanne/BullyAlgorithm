package bully;

public class Election extends Message {
	
	
	Election(final int from){
		this.from = from;
		this.type = "Election";
	}
	
}
