package bully;

public class TimeOutCoordinatorMessage extends Message{
	
	TimeOutCoordinatorMessage(final int from){
		this.from = from;
		this.type = "TimeOutCoordinatorMessage";
	}
}
