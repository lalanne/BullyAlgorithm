package bully;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class Process extends Thread{
	private final int identifier;
	MessageQueue messageQueue;
	Process[] processContainer;
	private boolean amICrashed = false;
	private boolean amIAlreadyStartAnElection = false;
	private int elected = -1;
	private TimerTask timerElectionExpired;
	private TimerTask timerCoordinatorExpired;
	private Timer electionTimer = null;
	private Timer coordinatorTimer = null;
	private int iterationCounter = 0;
	private final int ANSWER_TIMEOUT_MS = 320;
	private final int COORDINATOR_TIMEOUT_MS = 160;
	private final int PROCESS_DELAY = 300;
	private final int CRASH_DELAY = 10000;
	
	Process(final int identifier, MessageQueue messageQueue){
		this.identifier = identifier;
		this.messageQueue = messageQueue;
	}
	
	public void run() {
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "] Initializing process " +
						" id: " + this.getId() + 
						" identifier: " + this.identifier);
		execute();
	}
	
	public void cleanQueue(){
		messageQueue.clear();
	}
	
	public void setProcessContainer(Process[] processContainer){
		this.processContainer = processContainer;
	}
	
	private void execute(){
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
		Message message = messageQueue.get();
		processingReceivedMessage(message);
		++iterationCounter;
	}
	
	private void crashForever(){
		while(true){
			System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]I CRASHED, Not responding messages never again id[" + 
							identifier + "] Stopping timers too!!!!!!!!!!!!");
			if(electionTimer != null){
				stopAnswerMessageTimer();
			}
			try{
				Thread.sleep(CRASH_DELAY);
			}
			catch(InterruptedException ie){
				
			}
		}
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
	
	private void bullyOtherProcesses(){
		sendCoordinatorMessageToAllLowerProcesses();
		amIAlreadyStartAnElection = true;
	}
	
	private void startElection(){
		sendElectionMessageToBiggerProcesses();
		amIAlreadyStartAnElection = true;
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Starting timer for election in the process ["+ this.getIdentifier() +"]");
		startAnswerMessageTimer();
	}
	
	public void sendMessage(Message message){
		messageQueue.put(message);
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
		stopAnswerMessageTimer();
		this.elected = this.identifier;
		sendCoordinatorMessageToAllLowerProcesses();
	}
	
	private void processATimeOutCoordinatorMessage(Message message){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" receives a TimeOut Coordinator message from: " + message.getFrom()+ " stopping the timer. Starting a new election.");
		stopCoordinatorMessageTimer();
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
	
	void sendCoordinatorMessageToAllLowerProcesses(){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "] id[" +identifier+"]Sending coordinator messages to all processes with lower Ids");
		Message coordinator = new Coordinator(identifier);
		for(int i=0; i<identifier; ++i){
			processContainer[i].sendMessage(coordinator);
		}
	}
	
	void sendElectionMessageToBiggerProcesses(){
		Message election = new Election(identifier);
		for(int i=identifier+1; i<processContainer.length; ++i){
			processContainer[i].sendMessage(election);
		}
	}
	
	private void processAnAnswerMessage(Message message){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" receives an Answer message from: " + message.getFrom() + 
				" stopping the timer, there are more important processes than me alive, so cancelling the election.");
		amIAlreadyStartAnElection = false;
		stopAnswerMessageTimer();
		startCoordinatorMessageTimer();
	}
	
	private void startAnswerMessageTimer() {
		timerElectionExpired = new ElectionTimerExpired(this);
		electionTimer = new Timer();
		electionTimer.schedule(timerElectionExpired, ANSWER_TIMEOUT_MS);
	}
	
	private void stopAnswerMessageTimer() {
		if(electionTimer != null){
			electionTimer.cancel();
		}
	}
	
	private void startCoordinatorMessageTimer(){
		timerCoordinatorExpired = new CoordinatorTimerExpired(this);
		coordinatorTimer = new Timer();
		coordinatorTimer.schedule(timerCoordinatorExpired, COORDINATOR_TIMEOUT_MS);
	}
	
	private void stopCoordinatorMessageTimer() {
		if(coordinatorTimer != null){
			coordinatorTimer.cancel();
		}
	}
	
	private void processACoordinatorMessage(Message message){
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" receives an Coordinator message from: " + message.getFrom());
		elected = message.getFrom();
		System.out.println("[" +Calendar.getInstance().getTimeInMillis()+ "]Process id: " + identifier + 
				" My coordinator process is: [" + message.getFrom() + "]!!!!!!!");
		stopCoordinatorMessageTimer();
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
	
	public final int getIdentifier(){
		return identifier;
	}
}
