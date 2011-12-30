package bully;

import java.util.Calendar;

public class Communicator {
	private MessageQueue messageQueue;
	private final int identifier;
	
	Communicator(final int identifier, MessageQueue messageQueue){
		this.messageQueue = messageQueue;
		this.identifier = identifier;
	}
	
	public void sendMessage(Message message){
		messageQueue.put(message);
	}
	
	public void sendCoordinatorMessageToAllLowerProcesses(Process[] processContainer){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "] id[" +identifier+"]Sending coordinator messages to all processes with lower Ids");
		Message coordinator = new Coordinator(identifier);
		for(int i=0; i<identifier; ++i){
			processContainer[i].sendMessage(coordinator);
		}
	}
	
	public void sendElectionMessageToBiggerProcesses(Process[] processContainer){
		Message election = new Election(identifier);
		for(int i=identifier+1; i<processContainer.length; ++i){
			processContainer[i].sendMessage(election);
		}
	}
	
	public Message get(){
		return messageQueue.get();
	}
	
	public void clear(){
		messageQueue.clear();
	}
}
