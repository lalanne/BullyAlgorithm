package bully;

public class Process{
	private final int identifier;
	private Communicator communicator;
	private Protocol protocol;
	private MyThread myThread;
	
	Process(final int identifier, 
			Communicator communicator, 
			Protocol protocol,
			MyThread myThread){
		this.identifier = identifier;
		this.communicator = communicator;
		this.protocol = protocol;
		this.myThread = myThread;
	}
	
	public void start(){
		myThread.start();
	}
	
	public void setProcessContainer(Process[] processContainer){
		protocol.setProcessContainer(processContainer);
	}

	public void clear(){
		communicator.clear();
	}
	
	public final int getIdentifier(){
		return identifier;
	}
	
	public void sendMessage(Message message){
		communicator.sendMessage(message);
	}
}
