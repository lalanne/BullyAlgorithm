package bully;

import java.util.Calendar;

public class Protocol {
	private Timers timers;
	private boolean amICrashed = false;
	private boolean amIAlreadyStartAnElection = false;
	private int iterationCounter = 0;
	private Communicator communicator;
	Process[] processContainer;
	private final int PROCESS_DELAY = 300;
	private final int CRASH_DELAY = 10000;
	private int elected = -1;
	private final int identifier;
	
	Protocol(final int identifier, Communicator communicator, Timers timers){
		this.communicator = communicator;
		this.identifier = identifier;
		this.timers = timers;
	}
	
	public void execute(){
		while(true)
		{
			amICrashed = amICrashed();
			if(amICrashed){
				crashForever();
			}
			else{
				keepAlive();
			}
		}
	}
	
	private void crashForever(){
		while(true){
			System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]I CRASHED, Not responding messages never again id[" + 
							identifier + "] Stopping timers too!!!!!!!!!!!!");
			if(timers.getElectionTimer() != null){
				timers.stopAnswerMessageTimer();
			}
			try{
				Thread.sleep(CRASH_DELAY);
			}
			catch(InterruptedException ie){
				
			}
		}
	}
	
	public void setProcessContainer(Process[] processContainer){
		this.processContainer = processContainer;
	}
	
	private void keepAlive(){
		try{
			Thread.sleep(PROCESS_DELAY);
		}
		catch(InterruptedException ie){
	
		}
		
		if(amIStartingAnElection() && (amIAlreadyStartAnElection==false)){
			tryToStartAnElection();
		}
		else{
			//System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process [" + identifier + "] NOT starting an election");
		}
		//System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process [" + identifier + "] execution waiting for message......");
		Message message = communicator.get();
		processingReceivedMessage(message);
		++iterationCounter;
	}
	
	private boolean amIStartingAnElection(){
		if(iterationCounter == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	private boolean amICrashed(){
		RandomGenerator randomGenerator = new RandomGenerator(processContainer.length);
		if(randomGenerator.get() > processContainer.length/2){
			System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process " + this.identifier + " has failed!!!!!!!!");
			return true;
		}
		return false;
	}

	private void bullyOtherProcesses(){
		communicator.sendCoordinatorMessageToAllLowerProcesses(processContainer);
		amIAlreadyStartAnElection = true;
	}
	
	private void startElection(){
		communicator.sendElectionMessageToBiggerProcesses(processContainer);
		amIAlreadyStartAnElection = true;
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Starting timer for election in the process ["+ this.identifier +"]");
		timers.startAnswerMessageTimer();
	}
	
	private void tryToStartAnElection(){
		if(identifier == processContainer.length-1){
			System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]I have the biggest Id [" + identifier + "] Im the coordinator!!!!!");
			bullyOtherProcesses();
		}
		else{
			System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process [" + identifier + "] starting an election");
			startElection();
		}
	}
	
	private void processingReceivedMessage(Message message){
		if(!amICrashed){
			if(message.getType().equals("Election")){
				processAnElectionMessage(message);
			}
			else if(message.getType().equals("Answer")){
				processAnAnswerMessage(message);
			}
			else if(message.getType().equals("Coordinator")){
				processACoordinatorMessage(message);
			}
			else if(message.getType().equals("TimeOutAnswerMessage")){
				processATimeOutAnswerMessage(message);
			}
			else if(message.getType().equals("TimeOutCoordinatorMessage")){
				processATimeOutCoordinatorMessage(message);
			}
		}
		else{
			System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]process [" + identifier + "] I'm CRASHED!!!!!!!!  I cannot process Messages.");
		}
	}
	
	private void processATimeOutAnswerMessage(Message message){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" receives a TimeOut Answer message from: " + message.getFrom()+ " stopping the timer. And Im the coordinator!!!!!!!!!");
		timers.stopAnswerMessageTimer();
		this.elected = this.identifier;
		communicator.sendCoordinatorMessageToAllLowerProcesses(processContainer);
	}
	
	private void processATimeOutCoordinatorMessage(Message message){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" receives a TimeOut Coordinator message from: " + message.getFrom()+ " stopping the timer. Starting a new election.");
		timers.stopCoordinatorMessageTimer();
		if(elected == -1){
			System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
					" I dont have a current coordinator, so Start election! ");
			startElection();
		}
		else{
			System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
					" I have a current coordinator, NOT! going Start election! ");
		}
	}
	
	private void processAnAnswerMessage(Message message){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" receives an Answer message from: " + message.getFrom() + 
				" stopping the timer, there are more important processes than me alive, so cancelling the election.");
		amIAlreadyStartAnElection = false;
		timers.stopAnswerMessageTimer();
		timers.startCoordinatorMessageTimer();
	}
	
	private void processACoordinatorMessage(Message message){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" receives an Coordinator message from: " + message.getFrom());
		elected = message.getFrom();
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" My coordinator process is: [" + message.getFrom() + "]!!!!!!!");
		timers.stopCoordinatorMessageTimer();
	}

	private void processAnElectionMessage(Message message){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" receives an Election message from: " + message.getFrom());
		Message answer = new Answer(identifier);
		processContainer[message.getFrom()].sendMessage(answer);
		if(!amIAlreadyStartAnElection){
			System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process ["+ identifier +"] trying to start an election");
			tryToStartAnElection();
		}
	}

}
