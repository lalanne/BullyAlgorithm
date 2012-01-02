package bully;

public class TimeOutAnswerMessage extends Message{
	
	TimeOutAnswerMessage(final int from){
		this.from = from;
		this.type = "TimeOutAnswerMessage";
	}
}
