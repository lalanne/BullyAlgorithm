package bully;

public class Coordinator extends Message{
	
	Coordinator(final int from){
		this.from = from;
		this.type = "Coordinator";
	}
}
